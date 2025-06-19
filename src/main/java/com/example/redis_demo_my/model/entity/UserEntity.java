package com.example.redis_demo_my.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;


@RedisHash("User")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserEntity implements Serializable {
    @Id
    private Long id;
    private String name;

    @ToString.Exclude
    private Set<EventEntity> events = new HashSet<>();

}
