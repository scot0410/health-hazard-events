package com.healthhazard.events.infra;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaClient implements MessagePublisher{
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(Object message) {
        String TOPIC = "";
        String KEY = "";
        this.kafkaTemplate.send(TOPIC, KEY, message);
    }
}
