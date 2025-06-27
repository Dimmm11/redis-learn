package com.example.redis_demo_my.model.mappers;

import com.example.redis_demo_my.model.dto.User;
import com.example.redis_demo_my.model.entity.UserJpaEntity;
import com.example.redis_demo_my.model.entity.UserRedisEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {EventMapper.class})
public interface UserMapper {
    UserJpaEntity toUserJpaEntity(User user);
    User toDto(UserJpaEntity entity);

    UserRedisEntity toUserRedisEntity(User user);
    User toDto(UserRedisEntity entity);
}
