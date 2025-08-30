package com.example.redis_demo_my.service.kafka;

import com.example.redis_demo_my.configuration.properties.KafkaProperties;
import com.example.redis_demo_my.model.dto.Event;
import com.example.redis_demo_my.service.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventTopicListener {
    private final KafkaProperties kafkaProperties;
    private final EventService eventService;

    @KafkaListener(topics = "#{kafkaProperties.topic.name}", groupId = "#{kafkaProperties.groupId}")
    public void processMessage(Event event) {
      log.info(" <<===== Received event: {}, calling EventService to save", event);
      eventService.create(event);
    }
}
