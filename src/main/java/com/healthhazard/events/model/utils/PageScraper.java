package com.healthhazard.events.model.utils;

import com.healthhazard.events.model.dto.FoodRecall;
import com.healthhazard.events.model.dto.ProductDescription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PageScraper {
    public FoodRecall scrapePage(String rssTitle, String itemUrl, String pubDate, String agency) {
        try {
            var htmlDoc = Jsoup.connect(itemUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) RecallTracker/1.0")
                    .timeout(10000) // 10-second safety cutoff
                    .get();

            String companyName = extractTextByLabel(htmlDoc, "Company Name:");
            String brandName = extractTextByLabel(htmlDoc, "Brand Name:");
            String productName = extractTextByLabel(htmlDoc, "Product Description:");
            String recallReason = extractTextByLabel(htmlDoc, "Reason for Announcement:");

            // Step 4: Map directly to your clean Java records!
            var productDesc = ProductDescription.builder()
                    .companyName(companyName)
                    .productName(brandName)
                    .identifiers(List.of(productName))
                    .build();

            return FoodRecall.builder()
                    .eventId(generateInternalId(agency, pubDate))
                    .recallNumber("PENDING")
//                    .recallingFirm(agency)
                    .productDescription(productDesc)
                    .recallReason(recallReason)
                    .classification("Unclassified")
                    .status("Open")
                    .parsedDate(pubDate)
                    .distLocation("Unknown")
                    .sourceAgency(agency)
                    .infoUrl(itemUrl)
                    .build();

        } catch (RuntimeException | IOException ex) {
            log.error("error {}", ex.getMessage());
        }
        return null;
    }

    private String extractTextByLabel(Document doc, String labelText) {
        // Find the <dt> containing the target text, then select its immediate sibling <dd>
        Element element = doc.select("dt:contains(" + labelText + ") + dd").first();
        return (element != null) ? element.text().trim() : "Not Provided";
    }

//    private List<String> extractBrandNames(Document doc) {
//        Element brandElement = doc.select(".field--name-field-brand-name .field--item").first();
//        if (brandElement == null) return List.of();
//
//        return Arrays.stream(brandElement.text().split(","))
//                .map(String::trim)
//                .filter(s -> !s.isEmpty())
//                .toList();
//    }

    private String generateInternalId(String agency, String date) {
        return agency.toLowerCase() + "-" + date.hashCode() + "-" + System.nanoTime();
    }
}
