package com.healthhazard.events.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Results(
        @JsonProperty("skip") Integer skip,
        @JsonProperty("limit") Integer limit,
        @JsonProperty("total") Integer total) {}

