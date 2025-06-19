package com.example.redis_demo_my.repository;

import com.example.redis_demo_my.model.entity.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
}
