package com.healthhazard.events.model.dto;

import lombok.NonNull;

import java.time.LocalDate;

public record RssStreamRecord(
        @NonNull String title,
        @NonNull String description,
        @NonNull LocalDate pubDate,
        @NonNull String infoUrl,
        @NonNull String sourceAgency
) implements RecallData {
    @Override public String sourceAgency() { return sourceAgency; }
    @Override public String title() { return title; }
    @Override public String summary() { return description; }
    @Override public LocalDate eventDate() { return pubDate; }
    @Override public String sourceUrl() { return infoUrl; }
}
