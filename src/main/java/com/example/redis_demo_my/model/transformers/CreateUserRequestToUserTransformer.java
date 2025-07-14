package com.example.redis_demo_my.model.transformers;

import com.example.redis_demo_my.model.dto.Event;
import com.example.redis_demo_my.model.dto.Role;
import com.example.redis_demo_my.model.dto.User;
import com.example.redis_demo_my.model.dto.UserRequest;
import com.example.redis_demo_my.service.EventService;
import com.example.redis_demo_my.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Slf4j
@Component
public class CreateUserRequestToUserTransformer implements Transformer<UserRequest, User> {

    private final EventService eventService;
    private final RoleService roleService;

    @Override
    public User transform(UserRequest request) {
        Set<Event> events = eventService.findAll()
                .stream()
                .filter(event -> request.events().contains(event.id()))
                .collect(Collectors.toSet());
        Set<Role> roles = new HashSet<>();
        request.roles().forEach(role -> roles.add(Role.builder().userRole(role).build()));
        log.info("CreateUserRequest -> founded events: [{}]",
                events.stream().map(Event::id).map(String::valueOf).collect(Collectors.joining(", ")));
        return new User(request.id(),
                request.name(),
                request.password(),
                events,
                roles);
    }


}
