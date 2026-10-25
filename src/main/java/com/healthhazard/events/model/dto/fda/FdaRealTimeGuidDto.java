package com.healthhazard.events.model.dto.fda;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FdaRealTimeGuidDto {

    @JacksonXmlProperty(isAttribute = true, localName = "isPermaLink")
    private boolean isPermaLink;

    @JacksonXmlText
    private String value;
}
