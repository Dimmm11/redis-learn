package com.example.redis_demo_my.service;

import com.example.redis_demo_my.exception.UserNotFoundException;
import com.example.redis_demo_my.model.dto.User;
import com.example.redis_demo_my.model.entity.UserEntity;
import com.example.redis_demo_my.model.mappers.UserMapper;
import com.example.redis_demo_my.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final EventService eventService;
    private final UserMapper userMapper;


    public User getById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    public List<User> findAll() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(userMapper::toDto)
                .toList();
    }

    public User create(User user) {
        UserEntity entity = userMapper.toEntity(user);
        return userMapper.toDto(userRepository.save(entity));
    }

}
