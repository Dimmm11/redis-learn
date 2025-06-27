package com.example.redis_demo_my.service;

import com.example.redis_demo_my.exception.UserNotFoundException;
import com.example.redis_demo_my.model.dto.CreateUserRequest;
import com.example.redis_demo_my.model.dto.User;
import com.example.redis_demo_my.model.entity.UserJpaEntity;
import com.example.redis_demo_my.model.mappers.UserMapper;
import com.example.redis_demo_my.model.transformers.Transformer;
import com.example.redis_demo_my.repository.UserJpaRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserJpaRepository userJpaRepository;
    private final EventService eventService;
    private final UserMapper userMapper;
    private final Transformer<CreateUserRequest, User> createUserRequestToUserTransformer;

    public User getById(@NonNull UUID id) {
        return userJpaRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
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

}
