package com.example.redis_demo_my.service;

import com.example.redis_demo_my.exception.EventNotFoundException;
import com.example.redis_demo_my.model.dto.Event;
import com.example.redis_demo_my.model.entity.EventEntity;
import com.example.redis_demo_my.model.mappers.EventMapper;
import com.example.redis_demo_my.repository.EventRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final EventMapper mapper;

    public Event getById(@NonNull Long id) {
        return eventRepository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("no event by id: " + id));
    }

    public List<Event> findAll() {
        return StreamSupport.stream(eventRepository.findAll().spliterator(), false)
                .map(mapper::toDto)
                .toList();
    }

    public Event create(@NonNull Event event) {
        EventEntity entityToSave = mapper.toEntity(event);
        return mapper.toDto(eventRepository.save(entityToSave));
    }

    public Event update(Event event) {
        EventEntity eventFromDb = eventRepository.findById(event.id())
                .orElseThrow(() -> new EventNotFoundException(event.id().toString()));

        eventFromDb.setDescription(event.description());

        return mapper.toDto(eventRepository.save(eventFromDb));
    }

    public void deleteById(Long id) {
        eventRepository.deleteById(id);
    }

}
