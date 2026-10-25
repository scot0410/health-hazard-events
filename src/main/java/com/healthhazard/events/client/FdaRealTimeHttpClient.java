package com.healthhazard.events.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.healthhazard.events.exception.ClientErrorException;
import com.healthhazard.events.model.dto.fda.FdaRealTimeResponseDto;
import com.healthhazard.events.model.dto.fda.FdaRssFeedDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Service
@RequiredArgsConstructor
public class FdaRealTimeHttpClient {
    private final RestClient fdaRealTimeClient;
    private final XmlMapper xmlMapper;

    public FdaRssFeedDto fetchLiveFoodSafetyRecalls() {
        try {
            var response = fdaRealTimeClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/food-safety-recalls/rss.xml").build())
                    .retrieve()
                    .body(String.class);

            return xmlMapper.readValue(response, FdaRssFeedDto.class);
        } catch (RestClientResponseException ex) {
            throw new ClientErrorException(
                    "Client error on FDA RSS Feed call: %s, %s"
                            .formatted(ex.getStatusCode(), ex.getResponseBodyAsString()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
