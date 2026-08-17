package com.healthhazard.events.client;

import com.healthhazard.events.exception.ClientErrorException;
import com.healthhazard.events.model.dto.FdaRecallResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FdaHttpClient {
    private final RestClient fdaClient;

    public FdaRecallResponseDto fetchOngoingRecallData() {
        var pathogens = "listeria salmonella toxoplasma brucella";

        String searchQuery = String.format(
                "classification:\"Class I\" AND status:%s AND reason_for_recall:(%s)",
                "Ongoing",
                pathogens
        );

        try {
            return fdaClient.get()
                    .uri("/food/enforcement.json/?search={search}", searchQuery)
                    .retrieve()
                    .body(FdaRecallResponseDto.class);
        } catch (RestClientResponseException ex) {
            throw new ClientErrorException(
                    "Client error on FDA call: %s, %s"
                            .formatted(ex.getStatusCode(), ex.getResponseBodyAsString()));
        }
    }
}
