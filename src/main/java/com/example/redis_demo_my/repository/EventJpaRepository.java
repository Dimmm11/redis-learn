package com.example.redis_demo_my.repository;

import com.example.redis_demo_my.model.entity.EventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventJpaRepository extends JpaRepository<EventJpaEntity, UUID> {
    Optional<EventJpaEntity> findByName(String name);
}
