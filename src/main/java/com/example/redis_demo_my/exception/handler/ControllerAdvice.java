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
    private static final String CAUSED_BY_PREFIX = "Caused by:";

    @ExceptionHandler(RuntimeException.class)
    public ApiError handleRuntimeException(RuntimeException ex) {
        printSelectiveStackTrace(ex, PACKAGE_PREFIX.split(".exception")[0], CAUSED_BY_PREFIX);
        return ApiError.builder()
                .error(ex.getClass().getName())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private static void printSelectiveStackTrace(Throwable ex, String prefix, String causedBy) {
        for(StackTraceElement element: ex.getStackTrace()) {
            if(element.getClassName().startsWith(prefix) || element.toString().startsWith(causedBy)) {
                System.err.println("\t" + element);
            }
        }
    }
}
