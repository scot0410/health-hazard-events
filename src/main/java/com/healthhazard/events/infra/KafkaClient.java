package com.healthhazard.events.infra;

import com.healthhazard.events.model.FoodRecall;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaClient implements MessagePublisher {
    @Value("${fda.kafka.sync-topic}")
    private String topicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(List<FoodRecall> recalls) {
        log.info("Beginning Kafka transmission for {} recall payloads...", recalls.size());

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (FoodRecall recall : recalls) {
            try {
                var key = generateUniqueKey(recall);
                this.kafkaTemplate.send(topicName, key, recall)
                        .whenComplete(((result, ex) -> {
                            if (ex != null) {
                                log.error("ASYNC ERROR: Dropped record key [{}] with error {}",
                                        key,
                                        ex.getMessage());
                                failureCount.incrementAndGet();
                            } else {
                                successCount.incrementAndGet();
                            }
                        }));
            } catch (RuntimeException e) {
                log.error("LOCAL FAILURE: Skipping record due to data error: {}", e.getMessage());
            }
        }

        kafkaTemplate.flush();
        log.info("Batch synchronization completed and flushed to broker with success/failure counts: {}/{}",
                successCount, failureCount);
    }

    private static String generateUniqueKey(FoodRecall recall) {
        return "%s:%s".formatted(
                recall.sourceAgency().toLowerCase(),
                recall.recallNumber().toLowerCase());
    }
}
