package com.healthhazard.events.client;

import com.healthhazard.events.exception.ClientErrorException;
import com.healthhazard.events.model.dto.FdaRecallResponseDto;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FdaHttpClient {
    private final RestClient fdaClient;
    private final Validator validator;

    public FdaRecallResponseDto fetchOngoingRecallData() {
        var pathogens = "listeria salmonella toxoplasma brucella";

        String searchQuery = String.format(
                "classification:\"Class I\" AND status:%s AND reason_for_recall:(%s)",
                "Ongoing",
                pathogens);

        try {
            var result = fdaClient.get()
                    .uri("/food/enforcement.json/?search={search}", searchQuery)
                    .retrieve()
                    .body(FdaRecallResponseDto.class);

            var violations = validator.validate(result);
            if (!violations.isEmpty()) {
                throw new ClientErrorException("Validation failed for FDA payload. Violations [%s]".formatted(violations));
            }
            return result;
        } catch (RestClientResponseException ex) {
            throw new ClientErrorException(
                    "Client error on FDA call: %s, %s"
                            .formatted(ex.getStatusCode(), ex.getResponseBodyAsString()));
        }
    }
}
