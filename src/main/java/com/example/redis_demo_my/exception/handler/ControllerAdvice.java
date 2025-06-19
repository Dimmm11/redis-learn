package com.example.redis_demo_my.exception.handler;

import com.example.redis_demo_my.exception.error.ApiError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "com.example.redis_demo_my")
public class ControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ApiError handleRuntimeException(RuntimeException ex) {
        return ApiError.builder()
                .error(ex.getClass().getName())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
