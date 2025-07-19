package com.example.redis_demo_my.filter;

import com.example.redis_demo_my.configuration.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.redis_demo_my.utils.Constants.AUTHORIZATION;
import static com.example.redis_demo_my.utils.Constants.ROLES;
import static com.example.redis_demo_my.utils.Constants.USERNAME;

@RequiredArgsConstructor
@Slf4j
public class JWTTokenValidatorFilter extends OncePerRequestFilter {
    private final JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(AUTHORIZATION);
        if (jwt != null) {
            try {
                String secret = jwtProperties.getSecret();
                log.info("=====jwt secret: {}", secret);
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                Claims claims = Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(jwt)
                        .getPayload();

                String username = claims.get(USERNAME, String.class);
                String rolesString = claims.get(ROLES, String.class);
                Set<SimpleGrantedAuthority> authorities = Arrays.stream(rolesString.split(";"))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet());
                Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception ex) {
                log.error(ex.getMessage());
                throw new BadCredentialsException("invalid token");
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getServletPath().equals(JWTTokenGeneratorFilter.AUTH_PATH);
    }
}
