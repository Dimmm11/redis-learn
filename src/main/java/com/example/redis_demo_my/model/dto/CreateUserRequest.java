package com.example.redis_demo_my.model.dto;

import java.util.List;
import java.util.UUID;

public record CreateUserRequest(String name, List<UUID> events) {
}
