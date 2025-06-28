package com.example.redis_demo_my.service;

import com.example.redis_demo_my.configuration.properties.RedisProperties;
import com.example.redis_demo_my.model.dto.Event;
import com.example.redis_demo_my.model.entity.EventRedisEntity;
import com.example.redis_demo_my.model.mappers.EventMapper;
import com.example.redis_demo_my.repository.EventRedisRepository;
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
public class RedisService implements CrudOperations<Event> {
    private final RedisProperties redisProperties;
    private final EventRedisRepository eventRedisRepository;
    private final EventMapper eventMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String EVENT_PREFIX = "Event:";
    private static final String EVENT_INDEXES = EVENT_PREFIX.concat("keys");

    @Override
    public Optional<Event> getById(String id) {
        log.info("loading Event from Redis: {}", id);
        return eventRedisRepository.findById(id)
                .map(eventMapper::toDto);
    }

    @Override
    public List<Event> findAll() {
        log.info("loading all events from Redis");
        cleanExpiredKeys();

        Set<String> keys = redisTemplate.opsForSet().members(EVENT_INDEXES).stream()
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
        EventRedisEntity saved = saveWithTtl(entity, getCurrentTtl());
        eventMapper.toDto(saved);
    }

    @Override
    public void putAllToCache(List<Event> list) {
        log.info("put events to Redis");
        list.forEach(this::putToCache);
    }

    public EventRedisEntity saveWithTtl(EventRedisEntity entity, Duration ttl) {
        log.info("saving to Event to Redis: {}, ttl: {}", entity.getId(), ttl.get(ChronoUnit.SECONDS));
        String redisKey = buildRedisKey(entity);
        redisTemplate.opsForValue().set(redisKey, entity, ttl);
        redisTemplate.opsForSet().add(EVENT_INDEXES, redisKey);
        return entity;
    }

    private static String buildRedisKey(EventRedisEntity event) {
        return EVENT_PREFIX.concat(event.getId().toString());
    }

    public final Duration getCurrentTtl() {
        return Duration.of(redisProperties.getTtl(), ChronoUnit.SECONDS);
    }

    private void cleanExpiredKeys() {
        Set<String> keys = redisTemplate.opsForSet()
                .members(EVENT_INDEXES).stream()
                .map(String::valueOf)
                .collect(Collectors.toSet());
        log.info("redis keySet: {}", String.join(", ", keys));
        List<String> expiredKeys = keys.stream()
                .filter(key -> Boolean.FALSE.equals(redisTemplate.hasKey(key)))
                .toList();
        log.info("expired keys: {}", String.join(", ", expiredKeys));
        redisTemplate.opsForSet()
                .remove(EVENT_INDEXES, expiredKeys.toArray());
    }

    @Override
    public void cacheEvict(String key) {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
