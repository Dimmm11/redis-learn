package com.example.redis_demo_my.service;

import com.example.redis_demo_my.exception.UserNotFoundException;
import com.example.redis_demo_my.model.dto.CreateUserRequest;
import com.example.redis_demo_my.model.dto.User;
import com.example.redis_demo_my.model.entity.UserJpaEntity;
import com.example.redis_demo_my.model.mappers.UserMapper;
import com.example.redis_demo_my.model.transformers.Transformer;
import com.example.redis_demo_my.repository.UserJpaRepository;
import com.example.redis_demo_my.service.redis.RedisUserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements GenericService<User, UserJpaEntity> {
    private final UserJpaRepository userJpaRepository;
    private final RedisUserService redisUserService;
    private final UserMapper userMapper;
    private final Transformer<CreateUserRequest, User> createUserRequestToUserTransformer;

    public User getById(@NonNull UUID id) {
        return redisUserService.findOne(id.toString())
                .orElseGet(() -> {
                    log.warn("no User in Redis by id: {}", id);
                    User user = getFromDatabase(id);
                    redisUserService.putToCache(user);
                    return user;
                });
    }

    public List<User> findAll() {
        List<User> users = redisUserService.findAll();
        if (users.isEmpty()) {
            users = getAllFromDatabase();
            redisUserService.putAllToCache(users);
        }
        return users;
    }

    public User create(CreateUserRequest request) {
        log.info("CreateUserRequest: [{}]", request);
        User user = createUserRequestToUserTransformer.transform(request);
        UserJpaEntity entity = userMapper.toUserJpaEntity(user);
        UserJpaEntity saved = userJpaRepository.save(entity);
        log.info("saved user entity: [{}]", saved);
        return userMapper.toDto(saved);
    }

    public void deleteById(UUID id) {
        log.info("Delete user by id: [{}]", id);
        userJpaRepository.deleteById(id);
    }

    @Override
    public User getFromDatabase(UUID id) {
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
