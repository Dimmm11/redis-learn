package com.example.redis_demo_my.service;

import com.example.redis_demo_my.exception.EventNotFoundException;
import com.example.redis_demo_my.model.dto.Event;
import com.example.redis_demo_my.model.entity.EventJpaEntity;
import com.example.redis_demo_my.model.mappers.EventMapper;
import com.example.redis_demo_my.repository.EventJpaRepository;
import com.example.redis_demo_my.repository.EventRedisRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    private final EventJpaRepository eventJpaRepository;
    private final RedisService redisService;
    private final EventMapper mapper;

    public Event getById(@NonNull UUID id) {
        return redisService.getById(id.toString())
                .orElseGet(() -> {
                    log.warn("no Event in Redis by id: {}", id);
                    return getFromDatabase(id);
                });
    }

    public List<Event> findAll() {
        List<Event> events = redisService.findAll();
        if (events.isEmpty()) {
            log.info("loading all events from database");
            events = StreamSupport.stream(eventJpaRepository.findAll().spliterator(), false)
                    .map(mapper::toDto)
                    .toList();
            redisService.putAllToCache(events);
        }
        return events;
    }

    public Event create(@NonNull Event event) {
        EventJpaEntity entityToSave = mapper.toJpaEntity(event);
        EventJpaEntity saved = eventJpaRepository.save(entityToSave);
        Event dto = mapper.toDto(saved);
        redisService.putToCache(dto);
        return mapper.toDto(saved);
    }

    public Event update(Event event) {
        EventJpaEntity eventFromDb = eventJpaRepository.findById(event.id())
                .orElseThrow(() -> new EventNotFoundException(event.id().toString()));

        eventFromDb.setDescription(event.description());

        return mapper.toDto(eventJpaRepository.save(eventFromDb));
    }

    public void deleteById(UUID id) {
        eventJpaRepository.deleteById(id);
    }

    private Event getFromDatabase(UUID id) {
        log.info("loading Event from db: {}", id);
        return eventJpaRepository.findById(id)
                .map(jpaEntity -> {
                    Event dto = mapper.toDto(jpaEntity);
                    redisService.putToCache(dto);
                    return dto;
                })
                .orElseThrow(() -> new RuntimeException("no event by id: " + id));
    }

}
