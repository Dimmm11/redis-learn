package com.example.redis_demo_my.repository;

import com.example.redis_demo_my.model.entity.EventJpaEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface EventJpaRepository extends CrudRepository<EventJpaEntity, UUID> {

}
