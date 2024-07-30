package com.project.unispace.commons.config;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class RedisCleanupConfig {

    private final RedisTemplate<String, Object> redisTemplate;

    @PreDestroy
    public void cleanup() {
        // 모든 키 삭제
        Set<String> keys = redisTemplate.keys("*"); // 모든 키 조회
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys); // 조회한 키 삭제
        }
        System.out.println("Redis 데이터가 삭제되었습니다.");
    }
}
