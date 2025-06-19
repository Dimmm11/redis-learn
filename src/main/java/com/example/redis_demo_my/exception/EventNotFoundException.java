package com.example.redis_demo_my.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class EventNotFoundException extends RuntimeException{
    private static final String NOT_FOUND_MESSAGE = "Event not found: [%s]";
    public EventNotFoundException(String name) {
        super(NOT_FOUND_MESSAGE.formatted(name));
    }
}
