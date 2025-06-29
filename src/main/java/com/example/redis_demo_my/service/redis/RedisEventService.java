package com.example.redis_demo_my.service.redis;

import com.example.redis_demo_my.configuration.properties.RedisProperties;
import com.example.redis_demo_my.model.dto.Event;
import com.example.redis_demo_my.model.entity.EventRedisEntity;
import com.example.redis_demo_my.model.mappers.EventMapper;
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
@Service
@RequiredArgsConstructor
public class RedisEventService implements RedisCrudOperations<Event, EventRedisEntity> {
    private final RedisProperties redisProperties;
    private final EventMapper eventMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String EVENT_PREFIX = "Event:";
    private static final String EVENT_INDEXES = EVENT_PREFIX.concat(KEYS_SUFFIX);

    @Override
    public Optional<Event> findOne(String id) {
        log.info("loading Event from Redis: {}", id);
        return Optional.ofNullable(
                        redisTemplate.opsForValue()
                                .get(buildRedisKey(id))
                )
                .map(obj -> (EventRedisEntity) obj)
                .map(eventMapper::toDto);
    }

    @Override
    public List<Event> findAll() {
        log.info("loading events from Redis");
        cleanExpiredKeys();

        Set<String> keys = redisTemplate.opsForSet()
                .members(EVENT_INDEXES)
                .stream()
                .map(String::valueOf)
                .collect(Collectors.toSet());

        return redisTemplate.opsForValue()
                .multiGet(keys)
                .stream()
                .filter(Objects::nonNull)
                .map(obj -> (EventRedisEntity) obj)
                .map(eventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void putToCache(Event event) {
        EventRedisEntity entity = eventMapper.toRedisEntity(event);
        saveWithTtl(entity, getCurrentTtl());
    }

    @Override
    public void putAllToCache(List<Event> list) {
        log.info("put events to Redis");
        list.forEach(this::putToCache);
    }

    @Override
    public void saveWithTtl(EventRedisEntity entity, Duration ttl) {
        log.info("saving to Event to Redis: {}, ttl: {}", entity.getId(), ttl.get(ChronoUnit.SECONDS));
        String redisKey = buildRedisKey(entity.getId().toString());
        redisTemplate.opsForValue().set(redisKey, entity, ttl);
        redisTemplate.opsForSet().add(EVENT_INDEXES, redisKey);
    }

    @Override
    public String buildRedisKey(String id) {
        return EVENT_PREFIX.concat(id);
    }

    @Override
    public Duration getCurrentTtl() {
        return Duration.of(redisProperties.getTtl(), ChronoUnit.SECONDS);
    }

    @Override
    public void cleanExpiredKeys() {
        Set<String> keys = redisTemplate.opsForSet()
                .members(EVENT_INDEXES).stream()
                .map(String::valueOf)
                .collect(Collectors.toSet());
        List<String> expiredKeys = keys.stream()
                .filter(key -> Boolean.FALSE.equals(redisTemplate.hasKey(key)))
                .toList();
        if (!expiredKeys.isEmpty()) {
            redisTemplate.opsForSet()
                    .remove(EVENT_INDEXES, expiredKeys.toArray());
        }
    }

    @Override
    public void cacheEvict(String entityId) {
        String key = buildRedisKey(entityId);
        Boolean isDeleted = redisTemplate.delete(key);
        log.info("deleted from Redis: {}, success={}", key, isDeleted);

        redisTemplate.opsForSet()
                .remove(EVENT_INDEXES, key);
    }
}
