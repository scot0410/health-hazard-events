package com.healthhazard.events.model.dto.fda;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Builder;

@Builder
@JacksonXmlRootElement(localName = "rss")
public record FdaRssFeedDto(
        @JacksonXmlProperty(localName = "channel") FdaRealTimeResponseDto channel
) {
    @JsonCreator
    public FdaRssFeedDto {}
}
