package com.example.redis_demo_my.exception.security;

import com.example.redis_demo_my.model.dto.AuthenticationExceptionResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CustomBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setHeader("error-reason", "Authentication failed");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        // set response body
        String message = (authException != null && authException.getMessage() != null) ?
                authException.getMessage() : HttpStatus.UNAUTHORIZED.toString();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE.concat(";charset=UTF-8"));
        AuthenticationExceptionResponseDto dto = AuthenticationExceptionResponseDto.builder()
                .timestamp(LocalDateTime.now().toString())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(message)
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(request.getRequestURI())
                .build();
        String jsonResponse = objectMapper.writeValueAsString(dto);

        response.getWriter().write(jsonResponse);
    }
}
