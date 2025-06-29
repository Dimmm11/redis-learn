package com.example.redis_demo_my.service.redis;

import com.example.redis_demo_my.configuration.properties.RedisProperties;
import com.example.redis_demo_my.model.dto.User;
import com.example.redis_demo_my.model.entity.UserRedisEntity;
import com.example.redis_demo_my.model.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisUserService implements RedisCrudOperations<User, UserRedisEntity> {
    private final RedisProperties redisProperties;
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String USER_PREFIX = "User:";
    private static final String USER_INDEXES = USER_PREFIX.concat(KEYS_SUFFIX);

    @Override
    public void putToCache(User user) {
        UserRedisEntity userRedisEntity = userMapper.toUserRedisEntity(user);
        saveWithTtl(userRedisEntity, getCurrentTtl());
    }

    @Override
    public void putAllToCache(List<User> list) {
        log.info("put users to Redis");
        list.forEach(this::putToCache);
    }

    @Override
    public Optional<User> findOne(String id) {
        log.info("loading User from Redis: {}", id);
        return Optional.ofNullable(redisTemplate.opsForValue()
                        .get(buildRedisKey(id)))
                .map(obj -> (UserRedisEntity) obj)
                .map(userMapper::toDto);
    }

    @Override
    public List<User> findAll() {
        log.info("loading users from Redis");
        cleanExpiredKeys();

        Set<String> keys = redisTemplate.opsForSet()
                .members(USER_INDEXES)
                .stream()
                .map(String::valueOf)
                .collect(Collectors.toSet());

        return redisTemplate.opsForValue()
                .multiGet(keys)
                .stream()
                .filter(Objects::nonNull)
                .map(obj -> (UserRedisEntity) obj)
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public void cacheEvict(String key) {
        String redisKey = buildRedisKey(key);
        log.info("removing from Redis: {}", redisKey);
        redisTemplate.opsForSet()
                .remove(USER_INDEXES, redisKey);
        redisTemplate.delete(redisKey);

    }

    @Override
    public void saveWithTtl(UserRedisEntity entity, Duration ttl) {
        log.info("saving with ttl: {}", entity);
        String redisKey = buildRedisKey(entity.getId().toString());
        redisTemplate.opsForSet()
                .add(USER_INDEXES, redisKey);
        redisTemplate.opsForValue()
                .set(redisKey, entity, ttl);
    }

    @Override
    public Duration getCurrentTtl() {
        return Duration.of(redisProperties.getTtl(), ChronoUnit.SECONDS);
    }

    @Override
    public String buildRedisKey(String id) {
        return USER_PREFIX.concat(id);
    }

    @Override
    public void cleanExpiredKeys() {
        Set<String> keys = redisTemplate.opsForSet()
                .members(USER_INDEXES)
                .stream()
                .map(String::valueOf)
                .collect(Collectors.toSet());

        Set<String> expiredKeys = keys.stream()
                .filter(key -> !redisTemplate.hasKey(key))
                .collect(Collectors.toSet());

        if (!expiredKeys.isEmpty()) {
            redisTemplate.opsForSet()
                    .remove(USER_INDEXES, expiredKeys.toArray());
        }
    }
}
