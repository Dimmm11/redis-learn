package com.example.redis_demo_my.model.mappers;

import com.example.redis_demo_my.model.dto.Event;
import com.example.redis_demo_my.model.entity.EventEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventEntity toEntity(Event event);
    Event toDto(EventEntity entity);
}
