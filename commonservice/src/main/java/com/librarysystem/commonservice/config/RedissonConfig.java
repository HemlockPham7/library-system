package com.librarysystem.commonservice.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    @Value("${REDIS_HOST:localhost}")
    private String redisHost;

    @Value("${REDIS_PORT:6379}")
    private String redisPort;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        config.useSingleServer()
                .setAddress(String.format("redis://%s:%s", redisHost, redisPort))
                .setConnectTimeout(5000)
                .setTimeout(3000)
                .setRetryAttempts(3)
                .setRetryInterval(1500)
                .setConnectionPoolSize(64)          // Tăng lên 64 cho CPU 8 luồng
                .setConnectionMinimumIdleSize(16);  // Sẵn sàng 16 connection

        config.setThreads(4);
        config.setNettyThreads(8);                // Khớp với 8 luồng CPU i5-8250U

        return Redisson.create(config);
    }
}
