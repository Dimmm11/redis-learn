package com.example.redis_demo_my.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Objects;


@RedisHash("Event")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class EventEntity implements Serializable {
    @Id
    private Long id;
    private String name;
    private String description;

}
