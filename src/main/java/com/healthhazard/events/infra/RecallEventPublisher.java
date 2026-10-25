package com.healthhazard.events.infra;

import com.healthhazard.events.model.dto.FoodRecall;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public final class RecallEventPublisher implements MessagePublisher {
    @Value("${sqs.sqs-queue}")
    private String queue;

    private final SqsTemplate sqsTemplate;
    private final AtomicInteger success = new AtomicInteger(0);
    private final AtomicInteger failure = new AtomicInteger(0);

    public void publishEvents(List<FoodRecall> recalls) {
        for (var recall : recalls) {
            try {
                sqsTemplate.send(to -> to
                        .queue(queue)
                        .payload(recall)
                        .messageGroupId(recall.sourceAgency())
                        .messageDeduplicationId(recall.eventId()));
                success.incrementAndGet();
            } catch (RuntimeException e) {
                log.error("Event publisher failed at {} on error {}",
                        recall.recallNumber(),
                        e.getMessage());
                failure.incrementAndGet();
            }
        }
        log.info("Success/failure count {}/{}", success, failure);
    }
}
