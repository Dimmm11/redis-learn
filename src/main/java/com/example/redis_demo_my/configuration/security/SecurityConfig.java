package com.example.redis_demo_my.configuration.security;

import com.example.redis_demo_my.configuration.properties.JwtProperties;
import com.example.redis_demo_my.configuration.properties.OauthClientProperties;
import com.example.redis_demo_my.exception.security.CustomAccessDeniedHandler;
import com.example.redis_demo_my.exception.security.CustomBasicAuthenticationEntryPoint;
import com.example.redis_demo_my.filter.JWTTokenGeneratorFilter;
import com.example.redis_demo_my.filter.JWTTokenValidatorFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final ObjectMapper objectMapper;
    private final JwtProperties jwtProperties;
    private final OauthClientProperties oauthClientProperties;

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeHttpRequests((requests) -> requests
                .requestMatchers("/login").permitAll()
                .requestMatchers(HttpMethod.PUT).hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE).hasRole("ADMIN")
                .anyRequest().authenticated());

        http.csrf(AbstractHttpConfigurer::disable);

        http.formLogin(Customizer.withDefaults());
        http.httpBasic(Customizer.withDefaults());
        http.oauth2Login(Customizer.withDefaults());

        http.httpBasic(c -> c.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint(objectMapper)));

        http.exceptionHandling(config -> config
                .accessDeniedHandler(new CustomAccessDeniedHandler(objectMapper)));

        http.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        http.addFilterAfter(new JWTTokenGeneratorFilter(jwtProperties, objectMapper), BasicAuthenticationFilter.class);
        http.addFilterBefore(new JWTTokenValidatorFilter(jwtProperties), BasicAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = new ArrayList<>();
        registrations.add(githubClientRegistration());
        return new InMemoryClientRegistrationRepository(registrations);
    }

    public ClientRegistration githubClientRegistration() {
        log.info("clientId: {}, clientSecret: {}", oauthClientProperties.getClientId(), oauthClientProperties.getClientSecret());
        return CommonOAuth2Provider.GITHUB
                .getBuilder("github")
                .clientId(oauthClientProperties.getClientId())
                .clientSecret(oauthClientProperties.getClientSecret())
                .build();
    }
}
