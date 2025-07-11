package com.example.redis_demo_my.service;

import com.example.redis_demo_my.model.dto.Event;
import com.example.redis_demo_my.model.entity.EventJpaEntity;
import com.example.redis_demo_my.model.mappers.EventMapper;
import com.example.redis_demo_my.repository.EventJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
class EventServiceTest {
    private EventService eventService;
    private EventJpaRepository eventJpaRepository;
    private EventMapper mapper;
    private Event event;
    private EventJpaEntity eventEntity;
    private List<Event> events;

    @BeforeEach
    void setup() {
        eventJpaRepository = mock(EventJpaRepository.class);
        mapper = mock(EventMapper.class);
        eventService = new EventService(eventJpaRepository, mapper);
    }

    @Test
    void findAll() {
        event = buildEvent();

        eventEntity = mappedEntity(event);
        events = List.of(event);

        when(eventJpaRepository.findAll()).thenReturn(List.of(eventEntity));
        when(mapper.toDto(eventEntity)).thenReturn(event);
        when(mapper.toJpaEntity(event)).thenReturn(eventEntity);

        List<Event> result = eventService.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(event.id(), result.getFirst().id());
    }

    @Test
    void findOne() {
        event = buildEvent();
        eventEntity = mappedEntity(event);
        when(eventJpaRepository.findById(event.id())).thenReturn(Optional.of(eventEntity));
        when(mapper.toDto(eventEntity)).thenReturn(event);

        Event result = eventService.findOne(event.id());

        assertNotNull(result);
        assertEquals(event.id(), result.id());
        assertEquals(event.name(), result.name());
        assertEquals(event.description(), result.description());
    }

    @Test
    void create() {
        event = buildEvent();
        eventEntity = mappedEntity(event);

        when(mapper.toJpaEntity(event)).thenReturn(eventEntity);
        when(mapper.toDto(eventEntity)).thenReturn(event);
        when(eventJpaRepository.save(eventEntity)).thenReturn(eventEntity);

        Event result = eventService.create(event);

        assertNotNull(result);
    }

    private Event buildEvent() {
        UUID id = UUID.randomUUID();
        return new Event(id, "testName", "testDescription");
    }

    private EventJpaEntity mappedEntity(Event event) {
        return EventJpaEntity
                .builder()
                .id(event.id())
                .name(event.name())
                .description(event.description())
                .build();
    }

}