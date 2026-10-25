//package com.healthhazard.events.service;
//
//import com.healthhazard.events.recall.ExternalApiClient;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class RecallServiceTest {
//    @Mock
//    private ExternalApiClient externalApiClient;
//    @Mock
//    private MessagePublisher messagePublisher;
//    @InjectMocks
//    private RecallService service;
//
//    @Test
//    @DisplayName("processRecalls should publish response from external api call")
//    void processRecallsShouldPublishMessages() {
//        var recalls = List.of(TestUtil.getFoodRecall());
//        when(externalApiClient.fetchLatestRecalls()).thenReturn(recalls);
//
//        service.processRecalls();
//        verify(messagePublisher, times(1)).publish(recalls);
//    }
//}
