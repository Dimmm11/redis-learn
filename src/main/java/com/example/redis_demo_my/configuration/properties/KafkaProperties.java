package com.example.redis_demo_my.configuration.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class KafkaProperties {

    private Topic topic = new Topic();
    private String bootstrapServers;
    private String groupId;
    private String trustedPackages;


    @Getter
    @Setter
    @ToString
    public static class Topic {
        private String name;
        private int partitions;
        private int replicas;
    }

}
