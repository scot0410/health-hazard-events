package com.healthhazard.events.service;

import com.healthhazard.events.infra.MessagePublisher;
import com.healthhazard.events.recall.ExternalApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecallService {
    private final ExternalApiClient clientPort;
    private final MessagePublisher messagePublisher;

    @Async
    public void fetchLatestRecalls() {
        log.info("Fetching Latest Recalls...");

        clientPort.fetchLatestRecalls()
                .forEach(messagePublisher::publish);

        log.info("Latest Recalls - Saved.");
    }
}
