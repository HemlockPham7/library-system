package com.librarysystem.commonservice.services.cache;

import org.redisson.api.RedissonClient;
import org.redisson.api.redisnode.RedisNodes;
import org.springframework.stereotype.Service;

@Service
public class RedisHealthService {

    private final RedissonClient redissonClient;

    public RedisHealthService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * Send Ping request to Redis server.
     * @return true if Redis returns PONG, else false
     */
    public boolean pingRedis() {
        try {
            return redissonClient.getRedisNodes(RedisNodes.SINGLE).pingAll();
        } catch (Exception e) {
            return false;
        }
    }
}
