package com.example.redis_demo_my.model.mappers;

import com.example.redis_demo_my.model.dto.Event;
import com.example.redis_demo_my.model.entity.EventJpaEntity;
import com.example.redis_demo_my.model.entity.EventRedisEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventJpaEntity toJpaEntity(Event event);
    Event toDto(EventJpaEntity entity);

    EventRedisEntity toRedisEntity(Event event);

    Event toDto(EventRedisEntity entity);
}
