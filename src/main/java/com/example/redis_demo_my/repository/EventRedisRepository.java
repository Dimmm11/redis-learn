package com.example.redis_demo_my.repository;

import com.example.redis_demo_my.model.entity.EventRedisEntity;
import com.example.redis_demo_my.model.entity.UserRedisEntity;
import org.springframework.data.repository.CrudRepository;

public interface EventRedisRepository extends CrudRepository<EventRedisEntity, String> {
}
