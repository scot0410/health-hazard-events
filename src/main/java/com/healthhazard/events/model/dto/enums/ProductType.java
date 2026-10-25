package com.healthhazard.events.model.dto.enums;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ProductType {
    @JsonProperty("Food")
    FOOD,
    @JsonProperty("Cosmetics")
    COSMETICS,
    @JsonProperty("Biologics")
    BIOLOGICS,
    @JsonProperty("Drugs")
    DRUGS,
    @JsonProperty("Devices")
    DEVICES,
    @JsonProperty("Tobacco")
    TOBACCO
}
