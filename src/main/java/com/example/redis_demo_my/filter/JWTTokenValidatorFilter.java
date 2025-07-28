package com.example.redis_demo_my.filter;

import static com.example.redis_demo_my.utils.Constants.AUTHORIZATION;
import static com.example.redis_demo_my.utils.Constants.ROLES;
import static com.example.redis_demo_my.utils.Constants.USERNAME;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.redis_demo_my.configuration.properties.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JWTTokenValidatorFilter extends OncePerRequestFilter implements JwtTokenFilter {

    private final JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(AUTHORIZATION);
        if (jwt != null) {
            try {
                SecretKey secretKey = getSecretKey();
                Claims claims = getClaims(secretKey, jwt);
                Authentication authentication = buildAuthentication(claims);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception ex) {
                log.error(ex.getMessage());
                throw new BadCredentialsException("invalid token");
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        return request.getServletPath().equals(JWTTokenGeneratorFilter.AUTH_PATH);
    }

    @Override
    public JwtProperties getJwtProperties() {
        return this.jwtProperties;
    }

    private Claims getClaims(SecretKey secretKey, String jwt) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    private Authentication buildAuthentication(Claims claims) {
        String username = claims.get(USERNAME, String.class);
        String rolesString = claims.get(ROLES, String.class);
        Set<SimpleGrantedAuthority> authorities = Arrays.stream(rolesString.split(";"))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
}
