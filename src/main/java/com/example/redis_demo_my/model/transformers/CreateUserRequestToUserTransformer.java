package com.example.redis_demo_my.model.transformers;

import com.example.redis_demo_my.model.dto.CreateUserRequest;
import com.example.redis_demo_my.model.dto.Event;
import com.example.redis_demo_my.model.dto.User;
import com.example.redis_demo_my.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Slf4j
@Component
public class CreateUserRequestToUserTransformer implements Transformer<CreateUserRequest, User> {
    private final EventService eventService;


    @Override
    public User transform(CreateUserRequest request) {
        Set<Event> events = eventService.findAll()
                .stream()
                .filter(event -> request.events().contains(event.id()))
                .collect(Collectors.toSet());
        log.info("CreateUserRequest -> founded events: [{}]",
                events.stream().map(Event::id).map(String::valueOf).collect(Collectors.joining(", ")));
        return new User(null,
                request.name(),
                events);
    }
}
