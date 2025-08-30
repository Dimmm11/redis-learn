package com.example.redis_demo_my.service.kafka;

import com.example.redis_demo_my.configuration.properties.KafkaProperties;
import com.example.redis_demo_my.model.dto.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaProducer implements MessageProducer<Event> {

    private final KafkaProperties kafkaProperties;
    private final KafkaTemplate<String, Event> kafkaTemplate;

    @Override
    public void sendMessage(Event message) {
        try {
            log.info("__________Sending message='{}' to topic='{}'", message, kafkaProperties.getTopic());
            kafkaTemplate.sendDefault(message);
        } catch (Exception e) {
            log.error("Error sending message to kafka", e);
        }
    }
}
