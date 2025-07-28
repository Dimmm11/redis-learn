package com.example.redis_demo_my.service;

import com.example.redis_demo_my.exception.RoleNotFoundException;
import com.example.redis_demo_my.model.dto.Role;
import com.example.redis_demo_my.model.entity.RoleEntity;
import com.example.redis_demo_my.model.enums.UserRole;
import com.example.redis_demo_my.model.mappers.RoleMapper;
import com.example.redis_demo_my.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class RoleServiceTest {

    @Mock
    private RoleRepository repository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleService roleService;

    private RoleEntity testRoleEntity;
    private Role testRole;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testRoleEntity = new RoleEntity(testId, UserRole.ROLE_USER, new HashSet<>());
        
        testRole = Role.builder()
                .id(testId)
                .userRole(UserRole.ROLE_USER)
                .build();
    }

    @Test
    void findByName_WhenRoleExists_ShouldReturnRole() {
        // Given
        when(repository.findByName(UserRole.ROLE_USER)).thenReturn(Optional.of(testRoleEntity));
        when(roleMapper.toDto(testRoleEntity)).thenReturn(testRole);

        // When
        Role result = roleService.findByName(UserRole.ROLE_USER);

        // Then
        assertNotNull(result);
        assertEquals(testRole.getId(), result.getId());
        assertEquals(testRole.getUserRole(), result.getUserRole());
        
        verify(repository).findByName(UserRole.ROLE_USER);
        verify(roleMapper).toDto(testRoleEntity);
    }

    @Test
    void findByName_WhenRoleNotExists_ShouldThrowRoleNotFoundException() {
        // Given
        when(repository.findByName(UserRole.ROLE_ADMIN)).thenReturn(Optional.empty());

        // When & Then
        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class,
            () -> roleService.findByName(UserRole.ROLE_ADMIN));
        
        assertTrue(exception.getMessage().contains("Role not found: ROLE_ADMIN"));
        verify(repository).findByName(UserRole.ROLE_ADMIN);
        verify(roleMapper, never()).toDto(any());
    }

    @Test
    void findByName_WithNullRole_ShouldThrowException() {
        // When & Then
        assertThrows(RoleNotFoundException.class, () -> roleService.findByName(null));
    }

    @Test
    void findByName_WhenRepositoryThrowsException_ShouldPropagateException() {
        // Given
        when(repository.findByName(UserRole.ROLE_USER)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> roleService.findByName(UserRole.ROLE_USER));
        
        assertEquals("Database error", exception.getMessage());
        verify(repository).findByName(UserRole.ROLE_USER);
        verify(roleMapper, never()).toDto(any());
    }

    @Test
    void findByName_WithAdminRole_ShouldReturnAdminRole() {
        // Given
        RoleEntity adminEntity = new RoleEntity(UUID.randomUUID(), UserRole.ROLE_ADMIN, new HashSet<>());
        
        Role adminRole = Role.builder()
                .id(adminEntity.getId())
                .userRole(UserRole.ROLE_ADMIN)
                .build();

        when(repository.findByName(UserRole.ROLE_ADMIN)).thenReturn(Optional.of(adminEntity));
        when(roleMapper.toDto(adminEntity)).thenReturn(adminRole);

        // When
        Role result = roleService.findByName(UserRole.ROLE_ADMIN);

        // Then
        assertNotNull(result);
        assertEquals(UserRole.ROLE_ADMIN, result.getUserRole());
        assertEquals(adminEntity.getId(), result.getId());
        
        verify(repository).findByName(UserRole.ROLE_ADMIN);
        verify(roleMapper).toDto(adminEntity);
    }
} 