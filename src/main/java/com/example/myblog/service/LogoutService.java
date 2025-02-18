package com.example.myblog.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class LogoutService {
    private final StringRedisTemplate redisTemplate;

    public LogoutService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void logout(String refreshToken) {
        // 리프레시 토큰을 Redis 블랙리스트에 저장 (만료 시간 7일)
        redisTemplate.opsForValue().set(refreshToken, "blacklisted", Duration.ofDays(7));
    }

    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }
}
