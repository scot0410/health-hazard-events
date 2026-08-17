package com.healthhazard.events.model.dto;

import java.util.List;

public record FsisRecallResponseDto (
        List<FsisRecallDto> fsisRecalls
) {}
