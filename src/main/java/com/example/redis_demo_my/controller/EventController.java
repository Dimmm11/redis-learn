package com.example.redis_demo_my.controller;

import com.example.redis_demo_my.model.dto.Event;
import com.example.redis_demo_my.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/{id}")
    public Event getById(@PathVariable("id") Long id){
       return eventService.getById(id);
    }

    @GetMapping
    public List<Event> getAll(){
        return eventService.findAll();
    }

    @PostMapping
    public Event create(@RequestBody Event event) {
        return eventService.create(event);
    }

    @PutMapping
    public Event update(@RequestBody Event event) {
        return eventService.update(event);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            eventService.deleteById(id);
        }catch (Exception ex) {
            return ResponseEntity.internalServerError().body("failed: " + ex.getMessage());
        }
        return ResponseEntity.ok("success");
    }
}
