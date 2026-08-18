package com.healthhazard.events.client;

import com.healthhazard.events.config.FdaClientConfig;
import com.healthhazard.events.exception.ClientErrorException;
import com.healthhazard.events.model.dto.FdaRecallDto;
import com.healthhazard.events.model.dto.FdaRecallResponseDto;
import com.healthhazard.events.model.dto.Meta;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(FdaHttpClient.class)
@Import({FdaClientConfig.class, ValidationAutoConfiguration.class})
public class FdaHttpClientTest {
    @Autowired
    private Validator validator;

    @Autowired
    private FdaHttpClient client;

    @Autowired
    private MockRestServiceServer mockServer;

    @Test
    @DisplayName("Should successfully fetch and deserialize FDA data to FdaRecallResponseDto")
    void fetchOngoingRecallDataShouldFetchAndDeserializeFDAData() {

        var fdaRecallResponseDto = getFdaRecallResponseDtoString("TEST-123");

        mockServer
                .expect(requestTo(containsString("/food/enforcement.json/?search=")))
                .andRespond(
                        withSuccess(fdaRecallResponseDto, MediaType.APPLICATION_JSON));

        var result = client.fetchOngoingRecallData();
        assertThat(result, is(notNullValue()));
    }

    @Test
    @DisplayName("Should throw error on client side failure")
    void fetchOngoingRecallShouldThrowErrorOnClientSideFailure() {
        mockServer
                .expect(requestTo(containsString("/food/enforcement.json/?search=")))
                .andRespond(withBadRequest());
        var msg = assertThrows(ClientErrorException.class, () -> client.fetchOngoingRecallData());
        assertThat(msg.getMessage(), is("Client error on FDA call: 400 BAD_REQUEST, "));
    }

    @Test
    @DisplayName("Should throw error on server side failure")
    void fetchOngoingRecallShouldThrowErrorOnServerSideFailure() {
        mockServer
                .expect(requestTo(containsString("/food/enforcement.json/?search=")))
                .andRespond(withServerError());
        var msg = assertThrows(ClientErrorException.class, () -> client.fetchOngoingRecallData());

        assertThat(msg.getMessage(), is("Client error on FDA call: 500 INTERNAL_SERVER_ERROR, "));
    }

    @Test
    @DisplayName("Should throw error on validation constraints")
    void fetchOngoingRecallThrowErrorOnValidationConstraints() {
        var fdaRecallResponseDto = getFdaRecallResponseDtoString(null);

        mockServer
                .expect(requestTo(containsString("/food/enforcement.json/?search=")))
                .andRespond(
                        withSuccess(fdaRecallResponseDto, MediaType.APPLICATION_JSON));

        var msg = assertThrows(ClientErrorException.class, () -> client.fetchOngoingRecallData());
        assertThat(msg.getMessage(), containsString("Validation failed for FDA payload. Violations [[ConstraintViolationImpl{interpolatedMessage='Recall number is mandatory'"));
    }

    private static String getFdaRecallResponseDtoString(String recallNumber) {
        try {
            var meta = Meta.builder()
                    .build();

            var fdaRecallDto = FdaRecallDto.builder()
                    .recallNumber(recallNumber)
                    .build();

            var fdaRecallResponseDto = FdaRecallResponseDto.builder()
                    .meta(meta)
                    .results(List.of(fdaRecallDto))
                    .build();

            var mapper = new ObjectMapper();
            return mapper.writeValueAsString(fdaRecallResponseDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
