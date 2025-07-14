package com.example.redis_demo_my.configuration.redis;

import com.example.redis_demo_my.configuration.redis.properties.RedisProperties;
import com.example.redis_demo_my.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class RedisConfiguration {

    private final RedisProperties properties;
    private final ObjectMapper objectMapper;

    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return (builder -> builder
                .withCacheConfiguration(
                        Constants.EVENT,
                        redisCacheConfiguration()
                                .entryTtl(Duration.ofSeconds(properties.getTtl())))
                .withCacheConfiguration(
                        Constants.USER,
                        redisCacheConfiguration()
                                .entryTtl(Duration.ofSeconds(properties.getTtl() + 30))));
    }

    @Bean
    public RedisSerializationContext.SerializationPair<Object> valueSerializationPair() {
        RedisSerializer<Object> jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        return RedisSerializationContext.SerializationPair.fromSerializer(jsonRedisSerializer);
    }

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(valueSerializationPair());
    }
}
