package com.example.redis_demo_my.model.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Data
@RedisHash("User")
public class UserRedisEntity implements Serializable {
    @Id
    private UUID id;
    private String name;

    @EqualsAndHashCode.Exclude
    private Set<EventRedisEntity> events = new HashSet<>();
}
