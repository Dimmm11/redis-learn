package com.example.redis_demo_my.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public record User(UUID id, String name, @JsonIgnore String password, Set<Event> events, Set<Role> roles) implements Serializable {
}
