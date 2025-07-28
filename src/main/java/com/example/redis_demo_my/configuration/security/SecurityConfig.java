package com.example.redis_demo_my.configuration.security;

import com.example.redis_demo_my.configuration.properties.JwtProperties;
import com.example.redis_demo_my.exception.security.CustomAccessDeniedHandler;
import com.example.redis_demo_my.exception.security.CustomBasicAuthenticationEntryPoint;
import com.example.redis_demo_my.filter.JWTTokenGeneratorFilter;
import com.example.redis_demo_my.filter.JWTTokenValidatorFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final ObjectMapper objectMapper;
    private final JwtProperties jwtProperties;

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                .requestMatchers(HttpMethod.PUT).hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE).hasRole("ADMIN")
                .anyRequest().authenticated());

        http.csrf(AbstractHttpConfigurer::disable);

        http.formLogin(Customizer.withDefaults());
        http.httpBasic(Customizer.withDefaults());

        http.exceptionHandling(config -> config
                .authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint(objectMapper))
                .accessDeniedHandler(new CustomAccessDeniedHandler(objectMapper)));

        http.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterAfter(new JWTTokenGeneratorFilter(jwtProperties, objectMapper), BasicAuthenticationFilter.class);
        http.addFilterBefore(new JWTTokenValidatorFilter(jwtProperties, objectMapper), BasicAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
