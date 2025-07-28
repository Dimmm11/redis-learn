package com.example.redis_demo_my.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.redis_demo_my.exception.RoleNotFoundException;
import com.example.redis_demo_my.model.dto.Role;
import com.example.redis_demo_my.model.enums.UserRole;
import com.example.redis_demo_my.model.mappers.RoleMapper;
import com.example.redis_demo_my.repository.RoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {
    private final RoleRepository repository;
    private final RoleMapper roleMapper;

    public Role findByName(@NonNull UserRole userRole) {
        return repository.findByName(userRole)
                .map(roleMapper::toDto)
                .orElseThrow(() -> new RoleNotFoundException(String.valueOf(userRole)));
    }
}
