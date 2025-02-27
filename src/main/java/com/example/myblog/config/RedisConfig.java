package com.example.myblog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * ✅ Redis 설정 클래스
 *
 * - Spring Boot에서 Redis를 사용하기 위한 설정을 제공
 * - Lettuce를 사용하여 Redis와 연결
 * - 문자열 데이터를 저장하기 위해 직렬화 설정 적용
 */
@Configuration
public class RedisConfig {

    /**
     * ✅ Redis 연결 팩토리 (Lettuce 사용)
     *
     * - Redis 서버와 연결을 관리하는 역할
     * - LettuceConnectionFactory를 사용하여 싱글톤으로 관리
     *
     * @return RedisConnectionFactory 객체
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(); // ✅ 싱글톤 유지
    }

    /**
     * ✅ RedisTemplate 설정
     *
     * - Redis에 데이터를 저장하고 조회하는 역할
     * - 문자열 데이터를 저장하므로 Key & Value 직렬화를 StringRedisSerializer로 설정
     * - 기존의 RedisConnectionFactory Bean을 재사용
     *
     * @param redisConnectionFactory Redis 연결 팩토리
     * @return RedisTemplate<String, String> 객체
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory); // ✅ 기존 Bean 재사용

        // ✅ 문자열 데이터를 저장하므로, Key & Value 직렬화 설정 적용
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }
}
