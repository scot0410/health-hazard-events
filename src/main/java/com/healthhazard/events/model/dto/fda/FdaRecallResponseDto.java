package com.healthhazard.events.model.dto.fda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record FdaRecallResponseDto (
        @JsonProperty("meta") Meta meta,
        @JsonProperty("results") List<@Valid FdaRecallDto> results) {}
