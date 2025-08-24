package com.example.redis_demo_my.service.kafka;

import com.example.redis_demo_my.configuration.properties.KafkaProperties;
import com.example.redis_demo_my.model.dto.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventTopicListener {
    private final KafkaProperties kafkaProperties;

    @KafkaListener(topics = "#{@kafkaProperties.topicName}")
    public void processEvent(Event event) {

    }
}
