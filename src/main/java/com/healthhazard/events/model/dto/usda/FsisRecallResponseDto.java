package com.healthhazard.events.model.dto.usda;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record FsisRecallResponseDto (
        List<@Valid FsisRecallDto> fsisRecalls) {}
