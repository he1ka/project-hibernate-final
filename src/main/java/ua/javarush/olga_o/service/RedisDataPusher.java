package ua.javarush.olga_o.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import ua.javarush.olga_o.redis.CityCountry;

import java.util.List;

public class RedisDataPusher {

    private final RedisConnector redisConnector;
    private final ObjectMapper mapper;

    public RedisDataPusher(RedisConnector redisConnector) {
        this.redisConnector = redisConnector;
        this.mapper = new ObjectMapper();

    }

    public void pushToRedis(List<CityCountry> data) {
        try (StatefulRedisConnection<String, String> connection = redisConnector.getRedisClient().connect()) {
            RedisStringCommands<String, String> sync = connection.sync();
            for (CityCountry cityCountry : data) {
                try {
                    sync.set(String.valueOf(cityCountry.getId()), mapper.writeValueAsString(cityCountry));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
