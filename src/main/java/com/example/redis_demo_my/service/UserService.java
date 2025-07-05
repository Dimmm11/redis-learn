package com.example.redis_demo_my.service;

import com.example.redis_demo_my.exception.UserNotFoundException;
import com.example.redis_demo_my.model.dto.User;
import com.example.redis_demo_my.model.dto.UserRequest;
import com.example.redis_demo_my.model.entity.UserJpaEntity;
import com.example.redis_demo_my.model.entity.UserRedisEntity;
import com.example.redis_demo_my.model.mappers.UserMapper;
import com.example.redis_demo_my.model.transformers.Transformer;
import com.example.redis_demo_my.repository.UserJpaRepository;
import com.example.redis_demo_my.service.redis.RedisCrudOperations;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.redis_demo_my.service.redis.RedisCrudOperations.CACHE_MISS;

@Service
@Slf4j
public class UserService implements GenericCrudService<User> {
    private final UserJpaRepository userJpaRepository;
    private final RedisCrudOperations<User, UserRedisEntity> redis;
    private final UserMapper userMapper;
    private final Transformer<UserRequest, User> createUserRequestToUserTransformer;

    public UserService(UserJpaRepository userJpaRepository,
                       RedisCrudOperations<User, UserRedisEntity> redis,
                       UserMapper userMapper,
                       Transformer<UserRequest, User> createUserRequestToUserTransformer) {
        this.userJpaRepository = userJpaRepository;
        this.redis = redis;
        this.userMapper = userMapper;
        this.createUserRequestToUserTransformer = createUserRequestToUserTransformer;
    }

    @Override
    public User findOne(@NonNull UUID id) {
        return redis.findOne(id.toString())
                .orElseGet(() -> {
                    log.warn(CACHE_MISS, redis.getEntityName(), id);
                    User user = getFromDatabase(id);
                    redis.putToCache(user);
                    return user;
                });
    }

    @Override
    public List<User> findAll() {
        List<User> users = redis.findAll();
        if (users.isEmpty()) {
            users = getAllFromDatabase();
            redis.putAllToCache(users);
        }
        return users;
    }

    public User create(UserRequest request) {
        log.info("CreateUserRequest: [{}]", request);
        User user = createUserRequestToUserTransformer.transform(request);
        return create(user);
    }

    @Override
    public User create(User user) {
        UserJpaEntity entity = userMapper.toUserJpaEntity(user);
        UserJpaEntity saved = userJpaRepository.save(entity);
        log.info("saved user entity: [{}]", saved);
        User dto = userMapper.toDto(saved);
        redis.putToCache(dto);
        return dto;
    }

    public User update(UserRequest request) {
        log.info("updating user: {}", request);
        User user = createUserRequestToUserTransformer.transform(request);
        return update(user);
    }

    @Override
    public User update(@NonNull User user) {
        userJpaRepository.findById(user.id())
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(user.id().toString()));
        User updated = Optional.of(userJpaRepository.save(userMapper.toUserJpaEntity(user)))
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException("failed to update user: " + user.id()));
        redis.putToCache(updated);
        return updated;
    }

    public void delete(@NonNull UUID id) {
        log.info("Delete user by id: [{}]", id);
        redis.cacheEvict(id.toString());
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
}
