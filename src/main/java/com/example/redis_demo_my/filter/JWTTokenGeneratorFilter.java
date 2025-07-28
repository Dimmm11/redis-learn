package com.example.redis_demo_my.filter;

import static com.example.redis_demo_my.utils.Constants.ROLES;
import static com.example.redis_demo_my.utils.Constants.USERNAME;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.redis_demo_my.configuration.properties.JwtProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTTokenGeneratorFilter extends OncePerRequestFilter implements JwtTokenFilter {
    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper;
    public static final String AUTH_PATH = "/auth";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            SecretKey secretKey = getSecretKey();
            String jwt = buildJwt(authentication, secretKey);

            response.setContentType("application/json;charset=UTF-8");
            String json = objectMapper.writeValueAsString(Map.of("access_token", jwt));
            response.getWriter().write(json);
            return; // Don't continue the filter chain after sending response
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return !request.getServletPath().equals(AUTH_PATH);
    }

    @Override
    public JwtProperties getJwtProperties() {
        return this.jwtProperties;
    }

    private String buildJwt(Authentication authentication, SecretKey secretKey) {
        Date currentDate = new Date();
        Integer ttl = jwtProperties.getTtl();
        log.info("===jwt ttl: {}", ttl);
        return Jwts.builder()
                .issuer("dimmm")
                .subject("JWT Token")
                .claim(USERNAME, authentication.getName())
                .claim(ROLES, authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.joining(";")))
                .issuedAt(currentDate)
                .expiration(new Date(currentDate.getTime() + ttl * 1000))
                .signWith(secretKey)
                .compact();
    }
}
