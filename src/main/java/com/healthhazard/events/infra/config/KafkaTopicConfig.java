package com.healthhazard.events.infra.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${fda.kafka.sync-topic}")
    private String topicName;

    @Bean
//    @Profile({"local", "dev"})
    public NewTopic fdaSyncTopic() {
        return TopicBuilder.name(topicName)
                .partitions(3)       // Good default for scaling consumers later
                .replicas(1)         // Set to 1 for local dev (match your single broker)
                .build();
    }
}