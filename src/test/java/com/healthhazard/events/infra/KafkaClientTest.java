package com.healthhazard.events.infra;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.healthhazard.events.model.FoodRecall;
import com.healthhazard.events.utils.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class KafkaClientTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaClient kafkaClient;

    private final String testTopic = "fda-test-topic";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(kafkaClient, "topicName", testTopic);
    }

    @Test
    void should_PublishSuccessfully_When_DataIsValid() {
        var recall = TestUtil.getFoodRecall();
        List<FoodRecall> list = List.of(recall);

        // Simulate an instantly completed successful asynchronous future
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(mock(SendResult.class));
        when(kafkaTemplate.send(eq(testTopic), eq("fda:f-1234-2026"), eq(recall))).thenReturn(future);

        // Act
        kafkaClient.publish(list);

        // Assert
        verify(kafkaTemplate, times(1)).send(testTopic, "fda:f-1234-2026", recall);
        verify(kafkaTemplate, times(1)).flush(); // Verifies network delivery was forced
    }

    /**
     * 2. NEGATIVE LOCAL TEST: Verifies the fail-safe skipped-loop behavior.
     * Record 1 has broken/null data throwing an exception, but Record 2 must still get sent.
     */
//    @Test
//    void should_SkipBadRecordAndContinue_When_LocalKeyGenerationCrashes() {
//        // Arrange
//        // Record 1 will crash key generation because fields are null
//        FoodRecall badRecall = TestUtil.getFoodRecallWithRecallNumber(null);
//        // Record 2 is fully healthy
//        FoodRecall goodRecall = TestUtil.getFoodRecall();
//        List<FoodRecall> list = List.of(badRecall, goodRecall);
//
//        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(null);
//        // We only expect the good record to hit the template
//        when(kafkaTemplate.send(eq(testTopic), eq("fsis:042-2026"), eq(goodRecall))).thenReturn(future);
//
//        // Act
//        kafkaClient.publish(list);
//
//        // Assert
//        // The bad record should never make it to the network client layer
//        verify(kafkaTemplate, never()).send(eq(testTopic), contains("null"), eq(badRecall));
//        // The loop must have skipped the error and successfully processed the next item
//        verify(kafkaTemplate, times(1)).send(testTopic, "fda:f-1234-2026", goodRecall);
//        verify(kafkaTemplate, times(1)).flush();
//    }

    /**
     * 3. NEGATIVE ASYNCHRONOUS TEST: Verifies callback tracking.
     * Data is local-valid, but the real remote Kafka cluster safely drops the package.
     */
    @Test
    void should_LogAsyncFailureAndContinue_When_BrokerRejectsMessage() {
        // Arrange
        FoodRecall recall = TestUtil.getFoodRecall();
        List<FoodRecall> list = List.of(recall);

        // Simulate an asynchronous error response from the cluster network stack
        CompletableFuture<SendResult<String, Object>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Cluster Network Disconnect Timeout"));

        when(kafkaTemplate.send(eq(testTopic), eq("fda:f-1234-2026"), eq(recall))).thenReturn(failedFuture);

        // Act
        kafkaClient.publish(list);

        // Assert
        verify(kafkaTemplate, times(1)).send(testTopic, "fda:f-1234-2026", recall);
        verify(kafkaTemplate, times(1)).flush();
    }
}
