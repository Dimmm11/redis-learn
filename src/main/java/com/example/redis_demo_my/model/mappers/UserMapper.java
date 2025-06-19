package com.example.redis_demo_my.model.mappers;

import com.example.redis_demo_my.model.dto.User;
import com.example.redis_demo_my.model.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {EventMapper.class})
public interface UserMapper {
    UserEntity toEntity(User user);
    User toDto(UserEntity entity);
}
