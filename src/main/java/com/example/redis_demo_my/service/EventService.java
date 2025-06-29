package com.example.redis_demo_my.service;

import com.example.redis_demo_my.exception.EventNotFoundException;
import com.example.redis_demo_my.model.dto.Event;
import com.example.redis_demo_my.model.entity.EventJpaEntity;
import com.example.redis_demo_my.model.mappers.EventMapper;
import com.example.redis_demo_my.repository.EventJpaRepository;
import com.example.redis_demo_my.service.redis.RedisEventService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService implements GenericService <Event, EventJpaEntity>{
    private final EventJpaRepository eventJpaRepository;
    private final RedisEventService redisEventService;
    private final EventMapper mapper;

    public Event getById(@NonNull UUID id) {
        return redisEventService.findOne(id.toString())
                .orElseGet(() -> {
                    log.warn("no Event in Redis by id: {}", id);
                    Event event = getFromDatabase(id);
                    redisEventService.putToCache(event);
                    return event;
                });
    }

    public List<Event> findAll() {
        List<Event> events = redisEventService.findAll();
        if (events.isEmpty()) {
            log.info("loading all events from database");
            events = StreamSupport.stream(eventJpaRepository.findAll().spliterator(), false)
                    .map(mapper::toDto)
                    .toList();
            redisEventService.putAllToCache(events);
        }
        return events;
    }

    public Event create(@NonNull Event event) {
        EventJpaEntity entityToSave = mapper.toJpaEntity(event);
        EventJpaEntity saved = eventJpaRepository.save(entityToSave);
        Event dto = mapper.toDto(saved);
        redisEventService.putToCache(dto);
        return mapper.toDto(saved);
    }

    public Event update(Event event) {
        log.info("updating event: {}. New description: {}", event.id(), event.description());
        EventJpaEntity eventFromDb = eventJpaRepository.findById(event.id())
                .orElseThrow(() -> new EventNotFoundException(event.id().toString()));
        eventFromDb.setDescription(event.description());
        EventJpaEntity saved = eventJpaRepository.save(eventFromDb);
        Event dto = mapper.toDto(saved);
        redisEventService.putToCache(dto);
        return dto;
    }

    public void deleteById(UUID id) {
        eventJpaRepository.deleteById(id);
        redisEventService.cacheEvict(id.toString());
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
