package com.example.redis_demo_my.configuration.kafka;

import com.example.redis_demo_my.configuration.properties.KafkaProperties;
import com.example.redis_demo_my.model.dto.Event;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class KafkaConfiguration {

    private final KafkaProperties kafkaProperties;

    @PostConstruct
    public void logKafkaProperties() {
        log.info("KafkaProperties initialized successfully: {}", kafkaProperties);
    }

    @Bean
    NewTopic createTopic() {
        return TopicBuilder
                .name(kafkaProperties.getTopic().getName())
                .partitions(kafkaProperties.getTopic().getPartitions())
                .replicas(kafkaProperties.getTopic().getReplicas())
                .build();
    }

    @Bean
    DefaultKafkaConsumerFactory<String, Event> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getGroupId());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, kafkaProperties.getTrustedPackages());
        return new DefaultKafkaConsumerFactory<>(props, null,
                new JsonDeserializer<>(Event.class, false));
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, Event> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Event> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    DefaultKafkaProducerFactory<String, Event> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    KafkaTemplate<String, Event> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
