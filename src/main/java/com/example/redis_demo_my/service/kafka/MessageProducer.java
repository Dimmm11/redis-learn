package com.example.redis_demo_my.service.kafka;

public interface MessageProducer<T> {
    void sendMessage(T message);
}
