package com.example.redis_demo_my.model.entity;

import com.example.redis_demo_my.model.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {
    @Id
    private UUID id;
    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private UserRole name;
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Set<UserJpaEntity> users = new HashSet<>();
}
