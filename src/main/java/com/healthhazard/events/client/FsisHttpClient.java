package com.healthhazard.events.client;

import com.healthhazard.events.exception.ClientErrorException;
import com.healthhazard.events.model.dto.FsisRecallDto;
import com.healthhazard.events.model.dto.FsisRecallResponseDto;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FsisHttpClient {
    private final RestClient fsisClient;
    private final Validator validator;

    public FsisRecallResponseDto fetchRecallData() {
        try {
            var response = fsisClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/fsis/api/recall/v/1").build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<FsisRecallDto>>() {});

            var responseDto = FsisRecallResponseDto.builder()
                    .fsisRecalls(response)
                    .build();

            var violations = validator.validate(responseDto);
            if (!violations.isEmpty()) {
                throw new ClientErrorException("Validation failed for FSIS payload. Violations [%s]".formatted(violations));
            }
            return responseDto;
        } catch (RestClientResponseException ex) {
            throw new ClientErrorException(
                    "Client error on USDA call: %s, %s"
                            .formatted(ex.getStatusCode(), ex.getResponseBodyAsString()));
        }
    }
}