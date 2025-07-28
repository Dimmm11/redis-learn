package com.example.redis_demo_my.service;

import com.example.redis_demo_my.exception.EventNotFoundException;
import com.example.redis_demo_my.model.dto.Event;
import com.example.redis_demo_my.model.entity.EventJpaEntity;
import com.example.redis_demo_my.model.mappers.EventMapper;
import com.example.redis_demo_my.repository.EventJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class EventServiceTest {

    @Mock
    private EventJpaRepository eventJpaRepository;

    @Mock
    private EventMapper mapper;

    @InjectMocks
    private EventService eventService;

    private Event testEvent;
    private EventJpaEntity testEventEntity;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testEvent = new Event(testId, "Test Event", "Test Description");
        testEventEntity = EventJpaEntity.builder()
                .id(testId)
                .name("Test Event")
                .description("Test Description")
                .build();
    }

    @Test
    void findOne_WhenEventExists_ShouldReturnEvent() {
        // Given
        when(eventJpaRepository.findById(testId)).thenReturn(Optional.of(testEventEntity));
        when(mapper.toDto(testEventEntity)).thenReturn(testEvent);

        // When
        Event result = eventService.findOne(testId);

        // Then
        assertNotNull(result);
        assertEquals(testEvent.id(), result.id());
        assertEquals(testEvent.name(), result.name());
        assertEquals(testEvent.description(), result.description());
        
        verify(eventJpaRepository).findById(testId);
        verify(mapper).toDto(testEventEntity);
    }

    @Test
    void findOne_WhenEventNotExists_ShouldThrowRuntimeException() {
        // Given
        when(eventJpaRepository.findById(testId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> eventService.findOne(testId));
        
        assertTrue(exception.getMessage().contains("no event by id: " + testId));
        verify(eventJpaRepository).findById(testId);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void findAll_WhenEventsExist_ShouldReturnEventList() {
        // Given
        List<EventJpaEntity> entities = List.of(testEventEntity);
        when(eventJpaRepository.findAll()).thenReturn(entities);
        when(mapper.toDto(testEventEntity)).thenReturn(testEvent);

        // When
        List<Event> result = eventService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testEvent, result.get(0));
        
        verify(eventJpaRepository).findAll();
        verify(mapper).toDto(testEventEntity);
    }

    @Test
    void findAll_WhenNoEventsExist_ShouldReturnEmptyList() {
        // Given
        when(eventJpaRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Event> result = eventService.findAll();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(eventJpaRepository).findAll();
        verify(mapper, never()).toDto(any());
    }

    @Test
    void create_ShouldSaveAndReturnEvent() {
        // Given
        when(mapper.toJpaEntity(testEvent)).thenReturn(testEventEntity);
        when(eventJpaRepository.save(testEventEntity)).thenReturn(testEventEntity);
        when(mapper.toDto(testEventEntity)).thenReturn(testEvent);

        // When
        Event result = eventService.create(testEvent);

        // Then
        assertNotNull(result);
        assertEquals(testEvent, result);
        
        verify(mapper).toJpaEntity(testEvent);
        verify(eventJpaRepository).save(testEventEntity);
        verify(mapper).toDto(testEventEntity);
    }

    @Test
    void create_WithNullEvent_ShouldThrowException() {
        // When & Then
        assertThrows(Exception.class, () -> eventService.create(null));
        
        verify(mapper, never()).toJpaEntity(any());
        verify(eventJpaRepository, never()).save(any());
    }

    @Test
    void update_WhenEventExists_ShouldUpdateAndReturnEvent() {
        // Given
        Event updatedEvent = new Event(testId, "Updated Event", "Updated Description");
        EventJpaEntity existingEntity = EventJpaEntity.builder()
                .id(testId)
                .name("Test Event")
                .description("Test Description")
                .build();
        EventJpaEntity updatedEntity = EventJpaEntity.builder()
                .id(testId)
                .name("Test Event")
                .description("Updated Description")
                .build();

        when(eventJpaRepository.findById(testId)).thenReturn(Optional.of(existingEntity));
        when(eventJpaRepository.save(existingEntity)).thenReturn(updatedEntity);
        when(mapper.toDto(updatedEntity)).thenReturn(updatedEvent);

        // When
        Event result = eventService.update(updatedEvent);

        // Then
        assertNotNull(result);
        assertEquals(updatedEvent, result);
        assertEquals("Updated Description", existingEntity.getDescription());
        
        verify(eventJpaRepository).findById(testId);
        verify(eventJpaRepository).save(existingEntity);
        verify(mapper).toDto(updatedEntity);
    }

    @Test
    void update_WhenEventNotExists_ShouldThrowEventNotFoundException() {
        // Given
        when(eventJpaRepository.findById(testId)).thenReturn(Optional.empty());

        // When & Then
        EventNotFoundException exception = assertThrows(EventNotFoundException.class,
            () -> eventService.update(testEvent));
        
        assertTrue(exception.getMessage().contains(testId.toString()));
        verify(eventJpaRepository).findById(testId);
        verify(eventJpaRepository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    void delete_ShouldCallRepositoryDelete() {
        // When
        eventService.delete(testId);

        // Then
        verify(eventJpaRepository).deleteById(testId);
    }

    @Test
    void getFromDatabase_WhenEventExists_ShouldReturnEvent() {
        // Given
        when(eventJpaRepository.findById(testId)).thenReturn(Optional.of(testEventEntity));
        when(mapper.toDto(testEventEntity)).thenReturn(testEvent);

        // When
        Event result = eventService.getFromDatabase(testId);

        // Then
        assertNotNull(result);
        assertEquals(testEvent, result);
        
        verify(eventJpaRepository).findById(testId);
        verify(mapper).toDto(testEventEntity);
    }

    @Test
    void getFromDatabase_WhenEventNotExists_ShouldThrowRuntimeException() {
        // Given
        when(eventJpaRepository.findById(testId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> eventService.getFromDatabase(testId));
        
        assertTrue(exception.getMessage().contains("no event by id: " + testId));
        verify(eventJpaRepository).findById(testId);
        verify(mapper, never()).toDto(any());
    }

    @Test
    void getAllFromDatabase_WhenEventsExist_ShouldReturnEventList() {
        // Given
        List<EventJpaEntity> entities = List.of(testEventEntity);
        when(eventJpaRepository.findAll()).thenReturn(entities);
        when(mapper.toDto(testEventEntity)).thenReturn(testEvent);

        // When
        List<Event> result = eventService.getAllFromDatabase();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testEvent, result.get(0));
        
        verify(eventJpaRepository).findAll();
        verify(mapper).toDto(testEventEntity);
    }

    @Test
    void getAllFromDatabase_WhenNoEventsExist_ShouldReturnEmptyList() {
        // Given
        when(eventJpaRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<Event> result = eventService.getAllFromDatabase();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(eventJpaRepository).findAll();
        verify(mapper, never()).toDto(any());
    }

    @Test
    void findOne_WithNullId_ShouldThrowException() {
        // When & Then
        assertThrows(Exception.class, () -> eventService.findOne(null));
        
        verify(eventJpaRepository, never()).findById(any());
        verify(mapper, never()).toDto(any());
    }
}