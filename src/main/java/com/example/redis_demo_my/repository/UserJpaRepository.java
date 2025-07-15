package com.example.redis_demo_my.repository;

import com.example.redis_demo_my.model.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.LOAD;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {

    @EntityGraph(attributePaths = {"events", "roles"}, type = LOAD)
    @NonNull
    Optional<UserJpaEntity> findById(@NonNull UUID id);

    @EntityGraph(attributePaths = {"events", "roles"}, type = LOAD)
    @NonNull
    List<UserJpaEntity> findAll();

    @EntityGraph(attributePaths = {"roles"}, type = LOAD)
    Optional<UserJpaEntity> findByName(String name);
}
