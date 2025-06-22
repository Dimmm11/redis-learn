package com.example.redis_demo_my.repository;

import com.example.redis_demo_my.model.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.LOAD;

public interface UserRepository extends CrudRepository<UserEntity, Long> {

    @EntityGraph(attributePaths = {"events"}, type = LOAD)
    @NonNull
    Optional<UserEntity> findById(@NonNull Long id);

    @EntityGraph(attributePaths = {"events"}, type = LOAD)
    @NonNull
    List<UserEntity> findAll();
}
