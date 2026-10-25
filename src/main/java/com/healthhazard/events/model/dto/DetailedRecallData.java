package com.healthhazard.events.model.dto;

import lombok.Builder;
import lombok.NonNull;

import java.time.LocalDate;

@Builder
public record DetailedRecallData (
    @NonNull String eventId,
    @NonNull String recallNumber,
    String recallingFirm,
    ProductDescription productDescription,
    String recallReason,
    String classification,
    String status,
    LocalDate parsedDate,
    String distLocation,
    @NonNull String sourceAgency,
    String infoUrl
) implements RecallData {
        @Override public String sourceAgency() { return sourceAgency; }
        @Override public String title() { return productDescription != null ? productDescription.productName() : ""; }
        @Override public String summary() { return recallReason; }
        @Override public LocalDate eventDate() { return parsedDate; }
        @Override public String sourceUrl() { return infoUrl; }
}

