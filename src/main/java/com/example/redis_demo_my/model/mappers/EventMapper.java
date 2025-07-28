package com.example.redis_demo_my.model.mappers;

import com.example.redis_demo_my.model.dto.Event;
import com.example.redis_demo_my.model.entity.EventJpaEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Mapping(target = "users", ignore = true)
    EventJpaEntity toJpaEntity(Event event);

    Event toDto(EventJpaEntity entity);
}
