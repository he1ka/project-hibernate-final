package ua.javarush.olga_o;

import ua.javarush.olga_o.domain.City;
import ua.javarush.olga_o.redis.CityCountry;
import ua.javarush.olga_o.service.*;

import java.util.List;

public class Main {
    private final CityCountryTransformer cityCountryTransformer;

    private final RelationDbConnector relationDbConnector;
    private final RedisConnector redisConnector;
    private final DataFetcher dataFetcher;
    private final RedisDataPusher redisDataPusher;

    public Main() {
        relationDbConnector = new RelationDbConnector();
        dataFetcher = new DataFetcher(relationDbConnector);
        redisConnector = new RedisConnector();
        redisDataPusher = new RedisDataPusher(redisConnector);
        cityCountryTransformer = new CityCountryTransformer();
    }

    public static void main(String... param) {
        Main main = new Main();
        List<City> allCities = main.fetchData();
        List<CityCountry> preparedData = main.transformData(allCities);
        main.pushToRedis(preparedData);

        main.shutdown();
    }

    private void shutdown() {
        relationDbConnector.shutdown();
        redisConnector.shutdown();
    }

    private List<City> fetchData() {
        return dataFetcher.fetchData();
    }

    private List<CityCountry> transformData(List<City> cities) {
        return cityCountryTransformer.transformData(cities);
    }

    private void pushToRedis(List<CityCountry> data) {
        redisDataPusher.pushToRedis(data);
    }
}
