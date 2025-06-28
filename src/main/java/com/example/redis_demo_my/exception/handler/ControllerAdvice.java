package com.example.redis_demo_my.exception.handler;

import com.example.redis_demo_my.exception.error.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "com.example.redis_demo_my")
@Slf4j
public class ControllerAdvice {

    private static final String PACKAGE_PREFIX = ControllerAdvice.class.getPackageName();

    @ExceptionHandler(RuntimeException.class)
    public ApiError handleRuntimeException(RuntimeException ex) {
        printSelectiveStackTrace(ex, PACKAGE_PREFIX.split(".exception")[0]);
        return ApiError.builder()
                .error(ex.getClass().getName())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private static void printSelectiveStackTrace(Throwable ex, String prefix) {
        for(StackTraceElement element: ex.getStackTrace()) {
            if(element.getClassName().startsWith(prefix)) {
                System.err.println("\tat " + element);
            }
        }
    }
}
