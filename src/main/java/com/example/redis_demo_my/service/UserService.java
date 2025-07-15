package com.example.redis_demo_my.service;

import com.example.redis_demo_my.exception.UserNotFoundException;
import com.example.redis_demo_my.model.dto.User;
import com.example.redis_demo_my.model.dto.UserRequest;
import com.example.redis_demo_my.model.entity.EventJpaEntity;
import com.example.redis_demo_my.model.entity.RoleEntity;
import com.example.redis_demo_my.model.entity.UserJpaEntity;
import com.example.redis_demo_my.model.mappers.UserMapper;
import com.example.redis_demo_my.model.transformers.Transformer;
import com.example.redis_demo_my.repository.EventJpaRepository;
import com.example.redis_demo_my.repository.RoleRepository;
import com.example.redis_demo_my.repository.UserJpaRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.redis_demo_my.utils.Constants.USER;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements GenericCrudService<User> {
    private final UserJpaRepository userJpaRepository;
    private final RoleRepository roleRepository;
    private final EventJpaRepository eventRepository;
    private final UserMapper userMapper;
    private final Transformer<UserRequest, User> createUserRequestToUserTransformer;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Cacheable(cacheNames = USER, key = "#id")
    public User findOne(@NonNull UUID id) {
        return getFromDatabase(id);
    }

    @Override
    public List<User> findAll() {
        return getAllFromDatabase();
    }

    public User create(UserRequest request) {
        log.info("CreateUserRequest: [{}]", request);
        User user = createUserRequestToUserTransformer.transform(request);
        return create(user);
    }

    @Override
    @CachePut(cacheNames = USER, key = "#result.id", unless = "#result == null")
    public User create(User user) {
        UserJpaEntity entity = userMapper.toUserJpaEntity(user);
        Set<RoleEntity> roles = user.roles().stream()
                .map(role -> roleRepository.findByName(role.getUserRole()))
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
        entity.setRoles(roles);
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));

        UserJpaEntity saved = userJpaRepository.save(entity);
        log.info("saved user entity: [{}]", saved);
        return userMapper.toDto(saved);
    }

    public User update(UserRequest request) {
        log.info("updating user: {}", request);
        User user = createUserRequestToUserTransformer.transform(request);
        return update(user);
    }

    @Override
    @CachePut(cacheNames = USER, key = "#result.id", unless = "#result == null")
    public User update(@NonNull User user) {
        UserJpaEntity userFromDb = userJpaRepository.findById(user.id())
                .orElseThrow(() -> new UserNotFoundException(user.id().toString()));
        updateUserFields(user, userFromDb);
        return Optional.of(userJpaRepository.save(userFromDb))
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException("failed to update user: " + user.id()));
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = USER, key = "#id")
    })
    public void delete(@NonNull UUID id) {
        log.info("Delete user by id: [{}]", id);
        userJpaRepository.deleteById(id);
    }

    @Override
    public User getFromDatabase(@NonNull UUID id) {
        return userJpaRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    @Override
    public List<User> getAllFromDatabase() {
        return userJpaRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    private void updateUserFields(User user, UserJpaEntity userFromDb) {
        Optional.ofNullable(user.name())
                .ifPresent(userFromDb::setName);
        Optional.ofNullable(user.events())
                .ifPresent(events -> {
                    Set<EventJpaEntity> eventJpaEntities = events.stream()
                            .map(event -> eventRepository.findByName(event.name()))
                            .filter(Optional::isPresent)
                            .flatMap(Optional::stream)
                            .collect(Collectors.toSet());
                    log.info("updating events for user: {}, new events:[{}]", user.name(), eventJpaEntities.stream()
                            .map(EventJpaEntity::getName)
                            .collect(Collectors.joining(", ")));
                    userFromDb.setEvents(eventJpaEntities);
                });
        Optional.ofNullable(user.roles())
                .ifPresent(roles -> {
                    Set<RoleEntity> roleEntities = user.roles().stream()
                            .map(role -> roleRepository.findByName(role.getUserRole()))
                            .filter(Optional::isPresent)
                            .flatMap(Optional::stream)
                            .collect(Collectors.toSet());
                    log.info("updating roles for user: {}, new roles:[{}]", user.name(), roleEntities.stream()
                            .map(entity -> entity.getName().toString())
                            .collect(Collectors.joining(", ")));

                    userFromDb.setRoles(roleEntities);
                });
    }
}
