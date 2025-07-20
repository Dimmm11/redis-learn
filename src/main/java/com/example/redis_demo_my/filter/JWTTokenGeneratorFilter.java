package com.example.redis_demo_my.filter;

import com.example.redis_demo_my.configuration.properties.JwtProperties;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

import static com.example.redis_demo_my.utils.Constants.AUTHORIZATION;
import static com.example.redis_demo_my.utils.Constants.ROLES;
import static com.example.redis_demo_my.utils.Constants.USERNAME;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTTokenGeneratorFilter extends OncePerRequestFilter implements JwtTokenFilter{
    private final JwtProperties jwtProperties;
    static final String AUTH_PATH = "/auth";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            SecretKey secretKey = getSecretKey();
            String jwt = buildJwt(authentication, secretKey);
            response.setHeader(AUTHORIZATION, jwt);
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
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
                        .collect(Collectors.joining(";"))
                )
                .issuedAt(currentDate)
                .expiration(new Date(currentDate.getTime() + ttl * 1000))
                .signWith(secretKey)
                .compact();
    }
}
