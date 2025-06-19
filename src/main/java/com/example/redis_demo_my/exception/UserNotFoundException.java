package com.example.redis_demo_my.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class UserNotFoundException extends RuntimeException{
    private static final String USER_NOT_FOUND_MESSAGE = "User not found: %s";
    public UserNotFoundException(String message) {
        super(USER_NOT_FOUND_MESSAGE.formatted(message));
    }
}
