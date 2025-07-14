package com.example.redis_demo_my.model.dto;

import com.example.redis_demo_my.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role implements Serializable {
    private UUID id;
    private UserRole userRole;
}
