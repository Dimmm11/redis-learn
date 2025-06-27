package com.example.redis_demo_my.repository;

import com.example.redis_demo_my.model.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.LOAD;

public interface UserJpaRepository extends CrudRepository<UserJpaEntity, UUID> {

    @EntityGraph(attributePaths = {"events"}, type = LOAD)
    @NonNull
    Optional<UserJpaEntity> findById(@NonNull UUID id);

    @EntityGraph(attributePaths = {"events"}, type = LOAD)
    @NonNull
    List<UserJpaEntity> findAll();
}
