package com.example.redis_demo_my.service;

import com.example.redis_demo_my.configuration.properties.RedisProperties;
import com.example.redis_demo_my.model.dto.Event;
import com.example.redis_demo_my.model.entity.EventRedisEntity;
import com.example.redis_demo_my.model.mappers.EventMapper;
import com.example.redis_demo_my.repository.EventRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisService implements CrudOperations<Event> {
    private final RedisProperties redisProperties;
    private final EventRedisRepository eventRedisRepository;
    private final EventMapper eventMapper;

    @Override
    public Optional<Event> getById(String id) {
        log.info("loading Event from Redis: {}", id);
        return eventRedisRepository.findById(id)
                .map(eventMapper::toDto);
    }

    @Override
    public Event putToCache(Event event) {
        EventRedisEntity entity = eventMapper.toRedisEntity(event);
        entity.setTtl(redisProperties.getTtl());
        EventRedisEntity saved = saveWithTtl(entity, Duration.of(redisProperties.getTtl(), ChronoUnit.SECONDS));
        return eventMapper.toDto(saved);
    }

    @Override
    public void cacheEvict(String key) {
        throw new UnsupportedOperationException("not implemented yet");
    }

    public EventRedisEntity saveWithTtl(EventRedisEntity entity, Duration ttl) {
        log.info("saving to Event to Redis: {}, ttl: {}", entity.getId(), ttl.get(ChronoUnit.SECONDS));
        return eventRedisRepository.save(entity);
    }
}
