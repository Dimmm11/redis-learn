package com.example.redis_demo_my.exception.security;

import com.example.redis_demo_my.model.dto.AuthenticationExceptionResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setHeader("error-reason", "Access denied");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        // set response body
        String message = (accessDeniedException != null && accessDeniedException.getMessage() != null) ?
                accessDeniedException.getMessage() : "Authorization failed";
        response.setContentType(MediaType.APPLICATION_JSON_VALUE.concat(";charset=UTF-8"));
        AuthenticationExceptionResponseDto dto = AuthenticationExceptionResponseDto.builder()
                .timestamp(LocalDateTime.now().toString())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(message)
                .status(HttpStatus.FORBIDDEN.value())
                .path(request.getRequestURI())
                .build();
        String jsonResponse = objectMapper.writeValueAsString(dto);

        response.getWriter().write(jsonResponse);
    }
}
