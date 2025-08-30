package com.example.redis_demo_my.controller;

import com.example.redis_demo_my.model.dto.Event;
import com.example.redis_demo_my.service.EventService;
import com.example.redis_demo_my.service.kafka.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final KafkaProducer kafkaProducer;

    @GetMapping("/{id}")
    public Event getById(@PathVariable("id") UUID id) {
        return eventService.findOne(id);
    }

    @GetMapping
    public List<Event> getAll() {
        return eventService.findAll();
    }

    @PostMapping
    public String create(@RequestBody Event event) {
        kafkaProducer.sendMessage(event);
        return "Event sent to Kafka: %s".formatted(event);
    }

    @PutMapping
    public Event update(@RequestBody Event event) {
        return eventService.update(event);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public String delete(@RequestParam UUID id) {
        eventService.delete(id);
        return "Event deleted: %s".formatted(id);
    }
}
