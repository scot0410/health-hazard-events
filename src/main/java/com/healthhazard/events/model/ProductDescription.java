package com.healthhazard.events.model;

import lombok.Builder;

import java.util.List;

@Builder
public record ProductDescription (
        String companyName,
        String productName,
        List<String> identifiers
){}
