package com.example.redis_demo_my.model.mappers;

import com.example.redis_demo_my.model.dto.Role;
import com.example.redis_demo_my.model.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "userRole", source = "name")
    Role toDto(RoleEntity entity);

    @Mapping(target = "name", source = "userRole")
    @Mapping(target = "users", ignore = true)
    RoleEntity toEntity(Role dto);
}
