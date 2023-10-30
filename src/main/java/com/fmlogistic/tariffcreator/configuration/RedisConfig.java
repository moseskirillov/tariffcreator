package com.fmlogistic.tariffcreator.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Value("${redis.host}")
    private String HOST;

    @Value("${redis.port}")
    private int PORT;

    @Value("${redis.password}")
    private String PASSWORD;

    @Bean(destroyMethod = "close")
    public JedisPool jedisPool() {
        var pool = new JedisPoolConfig();
        pool.setMinIdle(16);
        pool.setMaxIdle(128);
        pool.setMaxTotal(128);
        pool.setJmxEnabled(false);
        pool.setTestOnBorrow(true);
        pool.setTestOnReturn(true);
        pool.setTestWhileIdle(true);
        pool.setBlockWhenExhausted(true);
        pool.setNumTestsPerEvictionRun(3);
        pool.setMinEvictableIdleTime(Duration.ofSeconds(60));
        pool.setTimeBetweenEvictionRuns(Duration.ofSeconds(30));
        return new JedisPool(pool, HOST, PORT, 2000, PASSWORD);
    }

}
