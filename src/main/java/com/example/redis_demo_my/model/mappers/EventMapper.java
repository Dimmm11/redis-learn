package com.example.redis_demo_my.model.mappers;

import com.example.redis_demo_my.model.dto.Event;
import com.example.redis_demo_my.model.entity.EventJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventJpaEntity toJpaEntity(Event event);
    Event toDto(EventJpaEntity entity);
}
