package com.healthhazard.events.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class FdaRealTimeClientConfig {
    @Bean
    public RestClient fdaRealTimeClient(RestClient.Builder builder) {
        return builder
                .baseUrl("https://www.fda.gov/about-fda/contact-fda/stay-informed/rss-feeds")
                .build();
    }
}
