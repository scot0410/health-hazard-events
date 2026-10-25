package com.healthhazard.events.model.dto.fda;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@JacksonXmlRootElement(localName = "channel")
public record FdaRealTimeResponseDto (
        @JacksonXmlProperty(localName = "title") String title,
        @JacksonXmlProperty(localName = "link") String link,
        @JacksonXmlProperty(localName = "description") String description,
        @JacksonXmlProperty(localName = "language") String language,
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "item") List<FdaRealTimeItemDto> items
) {
        @JsonCreator
        public FdaRealTimeResponseDto {} }
    
