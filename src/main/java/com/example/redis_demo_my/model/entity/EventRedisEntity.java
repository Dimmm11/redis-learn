package com.example.redis_demo_my.model.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@RedisHash(value = "Event")
public class EventRedisEntity implements Serializable {
    @Id
    private UUID id;
    private String name;
    private String description;
}
