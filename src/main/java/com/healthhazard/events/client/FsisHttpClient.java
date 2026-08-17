package com.healthhazard.events.client;

import com.healthhazard.events.exception.ClientErrorException;
import com.healthhazard.events.model.dto.FsisRecallResponseDto;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FsisHttpClient {
    private final RestClient fsisClient;

    public FsisRecallResponseDto fetchRecallData() {
        try {
            return fsisClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/fsis/api/recall/v/1").build())
                    .retrieve()
                    .body(FsisRecallResponseDto.class);
        } catch (RestClientResponseException ex) {
            throw new ClientErrorException(
                    "Client error on FDA call: %s, %s"
                            .formatted(ex.getStatusCode(), ex.getResponseBodyAsString()));
        }
    }
}