package com.example.redis_demo_my.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class TokenController {

    @PostMapping
    public ResponseEntity<String> auth() {
        return ResponseEntity.ok("success");
    }

}
