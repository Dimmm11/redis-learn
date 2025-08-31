package com.example.redis_demo_my.service.kafka.producer;

public interface MessageProducer<T> {
    void sendMessage(T message);
}
