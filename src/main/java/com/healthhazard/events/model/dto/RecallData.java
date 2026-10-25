package com.healthhazard.events.model.dto;

import java.time.LocalDate;

public interface RecallData {
    String sourceAgency();
    String title();
    String summary();
    LocalDate eventDate();
    String sourceUrl();
}
