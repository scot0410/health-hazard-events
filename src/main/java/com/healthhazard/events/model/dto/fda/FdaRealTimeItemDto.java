package com.healthhazard.events.model.dto.fda;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public record FdaRealTimeItemDto (
        @JacksonXmlProperty(localName = "title") String title,
        @JacksonXmlProperty(localName = "link") String link,
        @JacksonXmlProperty(localName = "description") String description,
        @JacksonXmlProperty(localName = "pubDate") String pubDate,
        // Handles the dc namespace prefix natively by matching the local element tag name
        @JacksonXmlProperty(localName = "creator") String creator,
        @JacksonXmlProperty(localName = "guid") FdaRealTimeGuidDto guid
        ) {
}



    