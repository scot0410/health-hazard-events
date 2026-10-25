package com.healthhazard.events.model.dto.fda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.util.Date;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record Meta(
        @JsonProperty("disclaimer") String disclaimer,
        @JsonProperty("license") String license,
        @JsonProperty("last_updated") Date lastUpdated,
        @JsonProperty("results") Results results) {}
