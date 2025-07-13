package com.example.redis_demo_my.service;

import com.example.redis_demo_my.exception.EventNotFoundException;
import com.example.redis_demo_my.model.dto.Event;
import com.example.redis_demo_my.model.entity.EventJpaEntity;
import com.example.redis_demo_my.model.mappers.EventMapper;
import com.example.redis_demo_my.repository.EventJpaRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static com.example.redis_demo_my.utils.Constants.ALL;
import static com.example.redis_demo_my.utils.Constants.EVENT;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventService implements GenericCrudService<Event> {
    private final EventJpaRepository eventJpaRepository;
    private final EventMapper mapper;

    @Override
    @Cacheable(cacheNames = EVENT, key = "#id")
    public Event findOne(@NonNull UUID id) {
        return getFromDatabase(id);
    }

    @Override
    public List<Event> findAll() {
        log.info("find all events");
        return StreamSupport.stream(eventJpaRepository.findAll().spliterator(), false)
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @CachePut(cacheNames = EVENT, key = "#result.id")
    public Event create(@NonNull Event event) {
        EventJpaEntity entityToSave = mapper.toJpaEntity(event);
        EventJpaEntity saved = eventJpaRepository.save(entityToSave);
        return mapper.toDto(saved);
    }

    @Override
    @CachePut(cacheNames = EVENT, key = "#result.id")
    public Event update(Event event) {
        log.info("updating event: {}. New description: {}", event.id(), event.description());
        EventJpaEntity eventFromDb = eventJpaRepository.findById(event.id())
                .orElseThrow(() -> new EventNotFoundException(event.id().toString()));
        eventFromDb.setDescription(event.description());
        EventJpaEntity saved = eventJpaRepository.save(eventFromDb);
        return mapper.toDto(saved);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(cacheNames = EVENT, key = "#id"),
            @CacheEvict(cacheNames = EVENT, key = ALL)
    })
    public void delete(UUID id) {
        eventJpaRepository.deleteById(id);
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
