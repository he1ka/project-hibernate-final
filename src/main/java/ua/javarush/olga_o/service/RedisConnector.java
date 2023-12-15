package ua.javarush.olga_o.service;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;

import static java.util.Objects.nonNull;

public class RedisConnector {
    private final RedisClient redisClient;

    public RedisConnector() {
        redisClient = prepareRedisClient();
    }

    public RedisClient prepareRedisClient() {
        RedisClient redisClient = RedisClient.create(RedisURI.create("localhost", 6379));
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {
            System.out.println("\nConnected to Redis\n");
        }
        return redisClient;
    }

    public RedisClient getRedisClient() {
        return redisClient;
    }

    public void shutdown() {
        if (nonNull(redisClient)) {
            redisClient.shutdown();
        }
    }
}
