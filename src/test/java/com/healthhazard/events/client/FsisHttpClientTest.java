package com.healthhazard.events.client;

import com.healthhazard.events.exception.ClientErrorException;
import com.healthhazard.events.model.dto.usda.FsisRecallDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(FsisHttpClient.class)
@Import(ValidationAutoConfiguration.class)
public class FsisHttpClientTest {
    @Autowired
    FsisHttpClient client;

    @Autowired
    MockRestServiceServer mockServer;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public RestClient fsisClient(RestClient.Builder builder) {
            return builder
                    .baseUrl("https://usda.gov")
                    .build();
        }
    }

    @Test
    @DisplayName("Should successfully fetch and deserialize USDA data to FsisRecallDto")
    void fetchRecallDataShouldFetchAndDeserializeFDAData() {
        var fsisRecallDto = getFsisRecallDtoString("TEST-123");

        mockServer
                .expect(requestTo(containsString("/fsis/api/recall/v/1")))
                .andRespond(
                        withSuccess(fsisRecallDto, MediaType.APPLICATION_JSON));

        var result = client.fetchRecallData();
        assertThat(result, is(notNullValue()));
    }

    @Test
    @DisplayName("Should throw error on client side failure")
    void fetchOngoingRecallShouldThrowErrorOnClientSideFailure() {
        mockServer
                .expect(requestTo(containsString("/fsis/api/recall/v/1")))
                .andRespond(withBadRequest());
        var msg = assertThrows(ClientErrorException.class, () -> client.fetchRecallData());
        assertThat(msg.getMessage(), is("Client error on USDA call: 400 BAD_REQUEST, "));
    }

    @Test
    @DisplayName("Should throw error on server side failure")
    void fetchOngoingRecallShouldThrowErrorOnServerSideFailure() {
        mockServer
                .expect(requestTo(containsString("/fsis/api/recall/v/1")))
                .andRespond(withServerError());
        var msg = assertThrows(ClientErrorException.class, () -> client.fetchRecallData());

        assertThat(msg.getMessage(), is("Client error on USDA call: 500 INTERNAL_SERVER_ERROR, "));
    }

    @Test
    @DisplayName("Should throw error on validation constraints")
    void fetchRecallDataShouldThrowErrorOnValidationConstraints() {
        var fsisRecallDto = getFsisRecallDtoString(null);

        mockServer
                .expect(requestTo(containsString("/fsis/api/recall/v/1")))
                .andRespond(
                        withSuccess(fsisRecallDto, MediaType.APPLICATION_JSON));

        var msg = assertThrows(ClientErrorException.class, () -> client.fetchRecallData());
        assertThat(msg, is(notNullValue()));
    }


    private static String getFsisRecallDtoString(String recallNumber) {
        try {

            var fsisRecallDto = FsisRecallDto.builder()
                    .recallNumber(recallNumber)
                    .build();

            var mapper = new ObjectMapper();
            return mapper.writeValueAsString(List.of(fsisRecallDto));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
