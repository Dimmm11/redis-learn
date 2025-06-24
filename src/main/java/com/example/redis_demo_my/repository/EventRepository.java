package com.example.redis_demo_my.repository;

import com.example.redis_demo_my.model.entity.EventEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface EventRepository extends CrudRepository<EventEntity, UUID> {

}
