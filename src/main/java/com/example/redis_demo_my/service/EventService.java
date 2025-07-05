package com.example.redis_demo_my.service;

import com.example.redis_demo_my.exception.EventNotFoundException;
import com.example.redis_demo_my.model.dto.Event;
import com.example.redis_demo_my.model.entity.EventJpaEntity;
import com.example.redis_demo_my.model.entity.EventRedisEntity;
import com.example.redis_demo_my.model.mappers.EventMapper;
import com.example.redis_demo_my.repository.EventJpaRepository;
import com.example.redis_demo_my.service.redis.RedisCrudOperations;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static com.example.redis_demo_my.service.redis.RedisCrudOperations.CACHE_MISS;

@Service
@Slf4j
public class EventService implements GenericCrudService<Event> {
    private final EventJpaRepository eventJpaRepository;
    private final RedisCrudOperations<Event, EventRedisEntity> redis;
    private final EventMapper mapper;

    public EventService(EventJpaRepository eventJpaRepository,
                        RedisCrudOperations<Event, EventRedisEntity> redis,
                        EventMapper mapper) {
        this.eventJpaRepository = eventJpaRepository;
        this.redis = redis;
        this.mapper = mapper;
    }

    @Override
    public Event findOne(@NonNull UUID id) {
        return redis.findOne(id.toString())
                .orElseGet(() -> {
                    log.warn(CACHE_MISS, redis.getEntityName(), id);
                    Event event = getFromDatabase(id);
                    redis.putToCache(event);
                    return event;
                });
    }

    @Override
    public List<Event> findAll() {
        List<Event> events = redis.findAll();
        if (events.isEmpty()) {
            log.info("loading all events from database");
            events = StreamSupport.stream(eventJpaRepository.findAll().spliterator(), false)
                    .map(mapper::toDto)
                    .toList();
            redis.putAllToCache(events);
        }
        return events;
    }

    @Override
    public Event create(@NonNull Event event) {
        EventJpaEntity entityToSave = mapper.toJpaEntity(event);
        EventJpaEntity saved = eventJpaRepository.save(entityToSave);
        Event dto = mapper.toDto(saved);
        redis.putToCache(dto);
        return mapper.toDto(saved);
    }

    @Override
    public Event update(Event event) {
        log.info("updating event: {}. New description: {}", event.id(), event.description());
        EventJpaEntity eventFromDb = eventJpaRepository.findById(event.id())
                .orElseThrow(() -> new EventNotFoundException(event.id().toString()));
        eventFromDb.setDescription(event.description());
        EventJpaEntity saved = eventJpaRepository.save(eventFromDb);
        Event dto = mapper.toDto(saved);
        redis.putToCache(dto);
        return dto;
    }

    @Override
    public void delete(UUID id) {
        eventJpaRepository.deleteById(id);
        redis.cacheEvict(id.toString());
    }

    @Override
    public Event getFromDatabase(UUID id) {
        log.info("loading Event from db: {}", id);
        return eventJpaRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("no event by id: " + id));
    }

    @Override
    public List<Event> getAllFromDatabase() {
        return StreamSupport.stream(eventJpaRepository.findAll().spliterator(), false)
                .map(mapper::toDto)
                .toList();
    }
}
