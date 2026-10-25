package com.healthhazard.events.model.dto;

import lombok.Builder;
import lombok.NonNull;

@Builder
public record FoodRecall (
        @NonNull String eventId,
        @NonNull String recallNumber,
        String recallingFirm,
        ProductDescription productDescription,
        String recallReason,
        String classification,
        String status,
        String parsedDate,
        String distLocation,
        @NonNull String sourceAgency,
        String infoUrl
) {}