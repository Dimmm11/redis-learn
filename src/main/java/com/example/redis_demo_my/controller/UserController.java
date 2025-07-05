package com.example.redis_demo_my.controller;

import com.example.redis_demo_my.model.dto.UserRequest;
import com.example.redis_demo_my.model.dto.User;
import com.example.redis_demo_my.service.UserService;
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
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<User> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable UUID id) {
        return userService.findOne(id);
    }

    @PostMapping
    public User create(@RequestBody UserRequest request) {
        return userService.create(request);
    }

    @PutMapping
    public User update(@RequestBody UserRequest request){
        return userService.update(request);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public String deleteById(@RequestParam UUID id) {
        userService.delete(id);
        return "Deleted user: %s".formatted(id);
    }

}
