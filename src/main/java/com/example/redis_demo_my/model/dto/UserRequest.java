package com.example.redis_demo_my.model.dto;

import com.example.redis_demo_my.model.enums.UserRole;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record UserRequest(UUID id, String name, String password, List<UUID> events, Set<UserRole> roles) {
}
