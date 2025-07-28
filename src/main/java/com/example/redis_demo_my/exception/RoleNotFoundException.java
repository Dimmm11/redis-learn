package com.example.redis_demo_my.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class RoleNotFoundException extends RuntimeException {
    private static final String ROLE_NOT_FOUND_MESSAGE = "Role not found: %s";
    
    public RoleNotFoundException(String message) {
        super(ROLE_NOT_FOUND_MESSAGE.formatted(message));
    }
} 