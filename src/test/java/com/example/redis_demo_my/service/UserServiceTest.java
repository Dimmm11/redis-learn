package com.example.redis_demo_my.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import com.example.redis_demo_my.exception.UserNotFoundException;
import com.example.redis_demo_my.model.dto.Event;
import com.example.redis_demo_my.model.dto.Role;
import com.example.redis_demo_my.model.dto.User;
import com.example.redis_demo_my.model.dto.UserRequest;
import com.example.redis_demo_my.model.entity.EventJpaEntity;
import com.example.redis_demo_my.model.entity.RoleEntity;
import com.example.redis_demo_my.model.entity.UserJpaEntity;
import com.example.redis_demo_my.model.enums.UserRole;
import com.example.redis_demo_my.model.mappers.UserMapper;
import com.example.redis_demo_my.model.transformers.Transformer;
import com.example.redis_demo_my.repository.EventJpaRepository;
import com.example.redis_demo_my.repository.RoleRepository;
import com.example.redis_demo_my.repository.UserJpaRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserServiceTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private EventJpaRepository eventRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Transformer<UserRequest, User> createUserRequestToUserTransformer;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserJpaEntity testUserEntity;
    private UserRequest testUserRequest;
    private Role testRole;
    private RoleEntity testRoleEntity;
    private Event testEvent;
    private EventJpaEntity testEventEntity;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();

        testRole = Role.builder()
                .id(UUID.randomUUID())
                .userRole(UserRole.ROLE_USER)
                .build();

        testRoleEntity = new RoleEntity(testRole.getId(), UserRole.ROLE_USER, new HashSet<>());

        testEvent = new Event(UUID.randomUUID(), "Test Event", "Test Description");

        testEventEntity = EventJpaEntity.builder()
                .id(testEvent.id())
                .name(testEvent.name())
                .description(testEvent.description())
                .build();

        testUser = new User(testId, "Test User", "password", Set.of(testEvent), Set.of(testRole));

        testUserEntity = new UserJpaEntity(testId, "Test User", "encodedPassword",
                Set.of(testEventEntity), Set.of(testRoleEntity));

        testUserRequest = new UserRequest(testId, "Test User", "password",
                List.of(testEvent.id()), Set.of(UserRole.ROLE_USER));
    }

    @Test
    void findOne_WhenUserExists_ShouldReturnUser() {
        // Given
        when(userJpaRepository.findById(testId)).thenReturn(Optional.of(testUserEntity));
        when(userMapper.toDto(testUserEntity)).thenReturn(testUser);

        // When
        User result = userService.findOne(testId);

        // Then
        assertNotNull(result);
        assertEquals(testUser.id(), result.id());
        assertEquals(testUser.name(), result.name());

        verify(userJpaRepository).findById(testId);
        verify(userMapper).toDto(testUserEntity);
    }

    @Test
    void findOne_WhenUserNotExists_ShouldThrowUserNotFoundException() {
        // Given
        when(userJpaRepository.findById(testId)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.findOne(testId));

        assertTrue(exception.getMessage().contains(testId.toString()));
        verify(userJpaRepository).findById(testId);
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void findOne_WithNullId_ShouldThrowException() {
        // When & Then
        assertThrows(Exception.class, () -> userService.findOne(null));

        verify(userJpaRepository, never()).findById(any());
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void findAll_WhenUsersExist_ShouldReturnUserList() {
        // Given
        List<UserJpaEntity> entities = List.of(testUserEntity);
        when(userJpaRepository.findAll()).thenReturn(entities);
        when(userMapper.toDto(testUserEntity)).thenReturn(testUser);

        // When
        List<User> result = userService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser, result.get(0));

        verify(userJpaRepository).findAll();
        verify(userMapper).toDto(testUserEntity);
    }

    @Test
    void findAll_WhenNoUsersExist_ShouldReturnEmptyList() {
        // Given
        when(userJpaRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<User> result = userService.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userJpaRepository).findAll();
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void createWithUserRequest_ShouldTransformAndCreateUser() {
        // Given
        when(createUserRequestToUserTransformer.transform(testUserRequest)).thenReturn(testUser);
        when(userMapper.toUserJpaEntity(testUser)).thenReturn(testUserEntity);
        when(roleRepository.findByName(UserRole.ROLE_USER)).thenReturn(Optional.of(testRoleEntity));
        when(passwordEncoder.encode(testUserEntity.getPassword())).thenReturn("encodedPassword");
        when(userJpaRepository.save(any(UserJpaEntity.class))).thenReturn(testUserEntity);
        when(userMapper.toDto(testUserEntity)).thenReturn(testUser);

        // When
        User result = userService.create(testUserRequest);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);

        verify(createUserRequestToUserTransformer).transform(testUserRequest);
        verify(userMapper).toUserJpaEntity(testUser);
        verify(roleRepository).findByName(UserRole.ROLE_USER);
        verify(passwordEncoder).encode(testUserEntity.getPassword());
        verify(userJpaRepository).save(any(UserJpaEntity.class));
        verify(userMapper).toDto(testUserEntity);
    }

    @Test
    void createWithUser_WhenRolesExist_ShouldCreateUserWithRoles() {
        // Given
        when(userMapper.toUserJpaEntity(testUser)).thenReturn(testUserEntity);
        when(roleRepository.findByName(UserRole.ROLE_USER)).thenReturn(Optional.of(testRoleEntity));
        when(passwordEncoder.encode(testUserEntity.getPassword())).thenReturn("encodedPassword");
        when(userJpaRepository.save(any(UserJpaEntity.class))).thenReturn(testUserEntity);
        when(userMapper.toDto(testUserEntity)).thenReturn(testUser);

        // When
        User result = userService.create(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);

        verify(userMapper).toUserJpaEntity(testUser);
        verify(roleRepository).findByName(UserRole.ROLE_USER);
        verify(passwordEncoder).encode(testUserEntity.getPassword());
        verify(userJpaRepository).save(any(UserJpaEntity.class));
        verify(userMapper).toDto(testUserEntity);
    }

    @Test
    void createWithUser_WhenRoleNotExists_ShouldCreateUserWithoutRoles() {
        // Given
        when(userMapper.toUserJpaEntity(testUser)).thenReturn(testUserEntity);
        when(roleRepository.findByName(UserRole.ROLE_USER)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(testUserEntity.getPassword())).thenReturn("encodedPassword");
        when(userJpaRepository.save(any(UserJpaEntity.class))).thenReturn(testUserEntity);
        when(userMapper.toDto(testUserEntity)).thenReturn(testUser);

        // When
        User result = userService.create(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);

        verify(roleRepository).findByName(UserRole.ROLE_USER);
        verify(userJpaRepository).save(any(UserJpaEntity.class));
    }

    @Test
    void updateWithUserRequest_ShouldTransformAndUpdateUser() {
        // Given
        when(createUserRequestToUserTransformer.transform(testUserRequest)).thenReturn(testUser);
        when(userJpaRepository.findById(testId)).thenReturn(Optional.of(testUserEntity));
        when(userJpaRepository.save(testUserEntity)).thenReturn(testUserEntity);
        when(userMapper.toDto(testUserEntity)).thenReturn(testUser);

        // When
        User result = userService.update(testUserRequest);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);

        verify(createUserRequestToUserTransformer).transform(testUserRequest);
        verify(userJpaRepository).findById(testId);
        verify(userJpaRepository).save(testUserEntity);
        verify(userMapper).toDto(testUserEntity);
    }

    @Test
    void updateWithUser_WhenUserExists_ShouldUpdateUser() {
        // Given
        when(userJpaRepository.findById(testId)).thenReturn(Optional.of(testUserEntity));
        when(userJpaRepository.save(testUserEntity)).thenReturn(testUserEntity);
        when(userMapper.toDto(testUserEntity)).thenReturn(testUser);

        // When
        User result = userService.update(testUser);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);

        verify(userJpaRepository).findById(testId);
        verify(userJpaRepository).save(testUserEntity);
        verify(userMapper).toDto(testUserEntity);
    }

    @Test
    void updateWithUser_WhenUserNotExists_ShouldThrowUserNotFoundException() {
        // Given
        when(userJpaRepository.findById(testId)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.update(testUser));

        assertTrue(exception.getMessage().contains(testId.toString()));
        verify(userJpaRepository).findById(testId);
        verify(userJpaRepository, never()).save(any());
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void updateWithUser_WithNullUser_ShouldThrowException() {
        // When & Then
        assertThrows(Exception.class, () -> userService.update((User) null));

        verify(userJpaRepository, never()).findById(any());
        verify(userJpaRepository, never()).save(any());
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {
        // When
        userService.delete(testId);

        // Then
        verify(userJpaRepository).deleteById(testId);
    }

    @Test
    void delete_WithNullId_ShouldThrowException() {
        // When & Then
        assertThrows(Exception.class, () -> userService.delete(null));

        verify(userJpaRepository, never()).deleteById(any());
    }

    @Test
    void getFromDatabase_WhenUserExists_ShouldReturnUser() {
        // Given
        when(userJpaRepository.findById(testId)).thenReturn(Optional.of(testUserEntity));
        when(userMapper.toDto(testUserEntity)).thenReturn(testUser);

        // When
        User result = userService.getFromDatabase(testId);

        // Then
        assertNotNull(result);
        assertEquals(testUser, result);

        verify(userJpaRepository).findById(testId);
        verify(userMapper).toDto(testUserEntity);
    }

    @Test
    void getFromDatabase_WhenUserNotExists_ShouldThrowUserNotFoundException() {
        // Given
        when(userJpaRepository.findById(testId)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getFromDatabase(testId));

        assertTrue(exception.getMessage().contains(testId.toString()));
        verify(userJpaRepository).findById(testId);
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void getAllFromDatabase_WhenUsersExist_ShouldReturnUserList() {
        // Given
        List<UserJpaEntity> entities = List.of(testUserEntity);
        when(userJpaRepository.findAll()).thenReturn(entities);
        when(userMapper.toDto(testUserEntity)).thenReturn(testUser);

        // When
        List<User> result = userService.getAllFromDatabase();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser, result.get(0));

        verify(userJpaRepository).findAll();
        verify(userMapper).toDto(testUserEntity);
    }

    @Test
    void getAllFromDatabase_WhenNoUsersExist_ShouldReturnEmptyList() {
        // Given
        when(userJpaRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<User> result = userService.getAllFromDatabase();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userJpaRepository).findAll();
        verify(userMapper, never()).toDto(any());
    }
}