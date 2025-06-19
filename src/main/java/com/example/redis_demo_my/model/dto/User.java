package com.example.redis_demo_my.model.dto;

import java.util.Set;

public record User(Long id, String name, Set<Event> events) {
}
