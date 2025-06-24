package com.example.redis_demo_my.model.dto;

import java.util.Set;
import java.util.UUID;

public record User(UUID id, String name, Set<Event> events) {
}
