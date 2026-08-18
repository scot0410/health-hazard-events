package com.healthhazard.events.model;

import lombok.Builder;
import lombok.NonNull;

import java.time.LocalDate;

@Builder
public record FoodRecall (
        @NonNull String recallNumber,
        String recallingFirm,
        ProductDescription productDescription,
        String reasonForRecall,
        String classification,
        String status,
        LocalDate parsedDate,
        String distLocation,
        String sourceAgency,
        String infoUrl
) {}