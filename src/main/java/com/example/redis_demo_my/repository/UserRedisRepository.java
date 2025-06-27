package com.example.redis_demo_my.repository;

import com.example.redis_demo_my.model.entity.UserRedisEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRedisRepository extends CrudRepository<UserRedisEntity, String> {
}
