package com.example.redis_demo_my.model.transformers;

import com.example.redis_demo_my.model.dto.Event;
import com.example.redis_demo_my.model.dto.Role;
import com.example.redis_demo_my.model.dto.User;
import com.example.redis_demo_my.model.dto.UserRequest;
import com.example.redis_demo_my.model.enums.UserRole;
import com.example.redis_demo_my.service.EventService;
import com.example.redis_demo_my.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Slf4j
@Component
public class CreateUserRequestToUserTransformer implements Transformer<UserRequest, User> {

    private final EventService eventService;

    @Override
    public User transform(UserRequest request) {
        Set<Event> events = readRequestEvents(request);
        Set<Role> roles = readRequestRoles(request);

        return new User(request.id(),
                request.name(),
                request.password(),
                events,
                roles);
    }

    private Set<Event> readRequestEvents(UserRequest request) {
        Set<Event> events = null;
        List<UUID> requestEventIds = request.events();
        if (Objects.nonNull(requestEventIds)) {
            events = new HashSet<>();
            for (UUID id : requestEventIds) {
                Event event = eventService.findOne(id);
                events.add(event);
            }
            log.info("CreateUserRequest -> founded events: [{}]",
                    events.stream().map(Event::id).map(String::valueOf).collect(Collectors.joining(", ")));
        }
        return events;
    }

    private Set<Role> readRequestRoles(UserRequest request) {
        Set<Role> roles = null;
        Set<UserRole> requestRoles = request.roles();
        if (Objects.nonNull(requestRoles)) {
            roles = new HashSet<>();
            for (UserRole userRole : requestRoles) {
                roles.add(Role.builder().userRole(userRole).build());
            }
            log.info("CreateUserRequest -> founded roles: [{}]",
                    roles.stream().map(Role::getUserRole).map(String::valueOf).collect(Collectors.joining(", ")));
        }
        return roles;
    }
}
