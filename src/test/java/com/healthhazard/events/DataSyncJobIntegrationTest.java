//package com.healthhazard.events;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.when;
//
//import java.time.Duration;
//import java.util.Collections;
//import java.util.List;
//import java.util.Map;
//
//import com.healthhazard.events.model.dto.FoodRecall;
//import com.healthhazard.events.recall.ExternalApiClient;
//
//import com.healthhazard.events.utils.TestUtil;
//import org.apache.kafka.clients.consumer.Consumer;
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.test.context.bean.override.mockito.MockitoBean; // Modern replacement for @MockBean
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//import org.springframework.kafka.support.serializer.JacksonJsonDeserializer; // Upgraded deserializer
//import org.springframework.kafka.test.utils.KafkaTestUtils;
//import org.testcontainers.containers.KafkaContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.utility.DockerImageName;
//
//@Testcontainers
//@SpringBootTest
//class DataSyncJobIntegrationTest {
//    @Container
//    static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));
//
//    @Autowired
//    private DataSyncJob dataSyncJob;
//
//    @MockitoBean
//    private ExternalApiClient externalApiClient;
//
//    @Value("${fda.kafka.sync-topic}")
//    private String topicName;
//
//    private Consumer<String, FoodRecall> testConsumer;
//
//    @DynamicPropertySource
//    static void overrideProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
//    }
//
//    @BeforeEach
//    void setUp() {
//        // 4. Configure test consumer properties using modern Jackson deserialization strings
//        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(kafka.getBootstrapServers(), "test-group", true);
//        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);
//        consumerProps.put(JacksonJsonDeserializer.TRUSTED_PACKAGES, "*");
//
//        DefaultKafkaConsumerFactory<String, FoodRecall> consumerFactory = new DefaultKafkaConsumerFactory<>(
//                consumerProps,
//                new StringDeserializer(),
//                new JacksonJsonDeserializer<>(FoodRecall.class, false)
//        );
//
//        testConsumer = consumerFactory.createConsumer();
//        testConsumer.subscribe(Collections.singletonList(topicName));
//    }
//
//    @AfterEach
//    void tearDown() {
//        if (testConsumer != null) {
//            testConsumer.close();
//        }
//    }
//
//    @Test
//    void should_ProcessBatch_And_PublishWithColonNamespacedKey() {
//        var mockRecall = TestUtil.getFoodRecall();
//
//        when(externalApiClient.fetchLatestRecalls()).thenReturn(List.of(mockRecall));
//
//        dataSyncJob.run();
//
//        var singleRecord = KafkaTestUtils.getSingleRecord(
//                testConsumer,
//                topicName,
//                Duration.ofSeconds(5));
//
//        assertThat(singleRecord.key()).isEqualTo("fda:f-1234-2026");
//
//        assertThat(singleRecord.value().recallNumber()).isEqualTo("F-1234-2026");
//        assertThat(dataSyncJob.getExitCode()).isEqualTo(0);
//    }
//
//    @Test
//    void should_Fail() {
//        var mockRecall = TestUtil.getFoodRecall();
//
//        when(externalApiClient.fetchLatestRecalls()).thenReturn(List.of(mockRecall));
//
//        dataSyncJob.run();
//
//        var singleRecord = KafkaTestUtils.getSingleRecord(
//                testConsumer,
//                topicName,
//                Duration.ofSeconds(5));
//
//        assertThat(singleRecord.key()).isEqualTo("fda:f-1234-2026");
//
//        assertThat(singleRecord.value().recallNumber()).isEqualTo("F-1234-2026");
//        assertThat(dataSyncJob.getExitCode()).isEqualTo(0);
//    }
//}
