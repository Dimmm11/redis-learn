package com.example.redis_demo_my.configuration.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
@NoArgsConstructor
public class JwtProperties {
    private String secret;
    private Integer ttl;
}
