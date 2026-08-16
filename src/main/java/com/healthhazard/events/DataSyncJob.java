package com.healthhazard.events;

import com.healthhazard.events.service.RecallService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSyncJob implements CommandLineRunner, ExitCodeGenerator {
    private int exitCode = 0;

    private final RecallService service;

    @Override
    public void run(String... args) {
        log.info("Executing scheduled batch sync sequence...");

        try {
            service.fetchLatestRecalls();
        } catch (Exception e) {
            log.error("CRITICAL: Data synchronization pipeline aborted due to unhandled failure", e);
            this.exitCode = 1;
        }
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
