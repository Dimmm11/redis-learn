package com.example.redis_demo_my.service.kafka;

import com.example.redis_demo_my.model.dto.Event;

public class KafkaConsumer implements MessageConsumer <Event>{
    @Override
    public void consumeMessage(Event message) {

    }
}
