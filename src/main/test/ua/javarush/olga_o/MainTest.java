package ua.javarush.olga_o;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.javarush.olga_o.domain.City;
import ua.javarush.olga_o.domain.CountryLanguage;
import ua.javarush.olga_o.redis.CityCountry;
import ua.javarush.olga_o.service.*;

import java.util.List;
import java.util.Set;

class MainTest {
    private ObjectMapper mapper;

    private CityCountryTransformer cityCountryTransformer;
    private RelationDbConnector relationDbConnector;
    private DataFetcher dataFetcher;
    private RedisConnector redisConnector;
    private RedisDataPusher redisDataPusher;

    @BeforeEach
    void setUp() {
        relationDbConnector = new RelationDbConnector();
        dataFetcher = new DataFetcher(relationDbConnector);

        redisConnector = new RedisConnector();
        redisDataPusher = new RedisDataPusher(redisConnector);

        mapper = new ObjectMapper();
        cityCountryTransformer = new CityCountryTransformer();
    }

    @Test
    void testPerformance() {
        List<City> allCities = dataFetcher.fetchData();
        List<CityCountry> preparedData = cityCountryTransformer.transformData(allCities);
        redisDataPusher.pushToRedis(preparedData);

        // Close current session, to make sure that we do a DB query, and not using cache
        relationDbConnector.getSessionFactory().getCurrentSession().close();

        // Get random 10 values
        // Because we didn't have a error handling -> add existing data only
        List<Integer> ids = List.of(3, 2545, 123, 4, 189, 89, 3458, 1189, 10, 102);

        long startRedis = System.currentTimeMillis();
        testRedisData(ids);
        long stopRedis = System.currentTimeMillis();

        long startMysql = System.currentTimeMillis();
        testMysqlData(ids);
        long stopMysql = System.currentTimeMillis();

        System.out.printf("%s:\t%d ms\n", "Redis", (stopRedis - startRedis));
        System.out.printf("%s:\t%d ms\n", "MySQL", (stopMysql - startMysql));

        relationDbConnector.shutdown();
        redisConnector.shutdown();
    }

    private void testRedisData(List<Integer> ids) {
        try (StatefulRedisConnection<String, String> connection = redisConnector.getRedisClient().connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (Integer id : ids) {
                String value = sync.get(String.valueOf(id));
                try {
                    mapper.readValue(value, CityCountry.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void testMysqlData(List<Integer> ids) {
        try (Session session = relationDbConnector.getSessionFactory().getCurrentSession()) {
            session.beginTransaction();
            for (Integer id : ids) {
                City city = relationDbConnector.getCityDAO().getById(id);
                Set<CountryLanguage> languages = city.getCountry().getLanguages();
            }
            session.getTransaction().commit();
        }
    }
}