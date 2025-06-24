package com.example.redis_demo_my.model.dto;

import java.util.UUID;

public record Event(UUID id, String name, String description) {
}
