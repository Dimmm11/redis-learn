package com.example.redis_demo_my.filter;

import com.example.redis_demo_my.configuration.properties.JwtProperties;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public interface JwtTokenFilter {
    default SecretKey getSecretKey() {
        String secret = getJwtProperties().getSecret();
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    JwtProperties getJwtProperties();
}
