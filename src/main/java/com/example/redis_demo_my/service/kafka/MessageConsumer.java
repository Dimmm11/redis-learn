package com.example.redis_demo_my.service.kafka;

public interface MessageConsumer <T>{
    void consumeMessage(T message);
}
