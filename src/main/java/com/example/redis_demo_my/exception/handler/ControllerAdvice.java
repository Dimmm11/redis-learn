package com.example.redis_demo_my.exception.handler;

import com.example.redis_demo_my.exception.error.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "com.example.redis_demo_my")
@Slf4j
public class ControllerAdvice {

    @ExceptionHandler(RuntimeException.class)
    public ApiError handleRuntimeException(RuntimeException ex) {
        ex.printStackTrace();
        return ApiError.builder()
                .error(ex.getClass().getName())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
