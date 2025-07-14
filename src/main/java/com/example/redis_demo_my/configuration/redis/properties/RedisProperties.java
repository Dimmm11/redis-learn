package com.example.redis_demo_my.configuration.redis.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(value = "spring.data.redis")
public class RedisProperties {
    private String host;
    private Integer port;
    private Integer ttl;
}
