package com.healthhazard.events.recall;

import com.healthhazard.events.client.FdaHttpClient;
import com.healthhazard.events.client.FdaRealTimeHttpClient;
import com.healthhazard.events.client.FsisHttpClient;
import com.healthhazard.events.model.dto.FoodRecall;
import com.healthhazard.events.model.dto.ProductDescription;
import com.healthhazard.events.model.dto.fda.*;
import com.healthhazard.events.model.dto.usda.FsisRecallDto;
import com.healthhazard.events.model.dto.usda.FsisRecallResponseDto;
import com.healthhazard.events.model.utils.PageScraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecallClient implements ExternalApiClient {
    private final FdaHttpClient fdaHttpClient;
    private final FsisHttpClient fsisHttpClient;
    private final FdaRealTimeHttpClient fdaRealTimeClient;
    private final PageScraper pageScraper;
    private static final String PENDING = "PENDING";

    @Override
    public List<FoodRecall> fetchLatestRecalls() {
        var fdaRealTimeRecallData = Optional.ofNullable(fdaRealTimeClient.fetchLiveFoodSafetyRecalls())
                .orElseGet(() -> FdaRssFeedDto.builder().build());

        var fdaRecallData = Optional.ofNullable(fdaHttpClient.fetchHistoricRecallData())
                .orElseGet(() -> FdaRecallResponseDto.builder().build());

        var fsisRecallData = Optional.ofNullable(fsisHttpClient.fetchRecallData())
                .orElseGet(() -> FsisRecallResponseDto.builder().build());

        return buildFoodRecall(fdaRealTimeRecallData.channel().items(), fdaRecallData.results(), fsisRecallData.fsisRecalls());
    }

    private List<FoodRecall> buildFoodRecall(List<FdaRealTimeItemDto> fdaRealTimeItemDtos,
                                                    List<FdaRecallDto> fdaRecallDtos,
                                                    List<FsisRecallDto> fsisRecallDtos) {
        var fdaLiveRecalls = safeMapToDto(fdaRealTimeItemDtos, this::fdaLiveRecallMapper, "FDA_RSS");
        var fdaRecalls = safeMapToDto(fdaRecallDtos, RecallClient::fdaRecallMapper, "FDA");
        var fsisRecalls = safeMapToDto(fsisRecallDtos, RecallClient::fsisRecallMapper, "USDA");
        return Stream.of(fdaLiveRecalls.stream(), fdaRecalls.stream(), fsisRecalls.stream())
                .flatMap(foodRecallStream -> foodRecallStream)
                .toList();
    }

    private FoodRecall fdaLiveRecallMapper(FdaRealTimeItemDto fdaRealTimeItemDto) {
        return pageScraper.scrapePage(
                fdaRealTimeItemDto.title(),
                fdaRealTimeItemDto.link(),
                fdaRealTimeItemDto.pubDate(),
                fdaRealTimeItemDto.creator());
    }

    private static FoodRecall fdaRecallMapper(FdaRecallDto fdaRecallDto) {
        var reportDate = fdaRecallDto.reportDate();
        var parsedDate = reportDate != null
                ? LocalDate.parse(reportDate, DateTimeFormatter.ofPattern("yyyyMMdd"))
                : null;
        var simpleLocation = String.format("%s, %s, %s", fdaRecallDto.city(), fdaRecallDto.state(), fdaRecallDto.country());

        var productDescription = buildProductDescription(fdaRecallDto);
        return FoodRecall.builder()
                .eventId(UUID.randomUUID().toString())
                .recallNumber(fdaRecallDto.recallNumber())
                .recallingFirm(fdaRecallDto.recallingFirm())
                .productDescription(productDescription)
                .recallReason(fdaRecallDto.reasonForRecall())
                .classification(fdaRecallDto.classification())
                .status(fdaRecallDto.status())
                .parsedDate(String.valueOf(parsedDate))
                .distLocation(simpleLocation)
                .sourceAgency("FDA")
                .build();
    }

    private static ProductDescription buildProductDescription(FdaRecallDto fdaRecallDto) {
        return ProductDescription.builder()
                .companyName(fdaRecallDto.recallingFirm())
                .productName(fdaRecallDto.productDescription())
                .identifiers(List.of(fdaRecallDto.productDescription(), fdaRecallDto.codeInfo()))
                .build();
    }

    private static FoodRecall fsisRecallMapper(FsisRecallDto fsisRecallDto) {
        var simpleLocation = fsisRecallDto.states() != null
                ? String.join(", ", fsisRecallDto.states())
                : "Unknown";
        var reason = fsisRecallDto.recallReason() != null
                ? String.join(", ", fsisRecallDto.recallReason())
                : "Unknown";

        var productDescription = collectProductDescription(fsisRecallDto);
        return FoodRecall.builder()
                .eventId(UUID.randomUUID().toString())
                .recallNumber(fsisRecallDto.recallNumber())
                .recallingFirm(fsisRecallDto.mediaContact())
                .productDescription(productDescription)
                .recallReason(reason)
                .classification(fsisRecallDto.riskLevel())
                .status(fsisRecallDto.recallType())
                .parsedDate(fsisRecallDto.recallDate())
                .distLocation(simpleLocation)
                .sourceAgency("USDA")
                .infoUrl(fsisRecallDto.recallUrl())
                .build();
    }

    private static ProductDescription collectProductDescription(FsisRecallDto fsisRecallDto) {
        //"Rosina Food Products, Inc. Recalls Ready-To-Eat Frozen Meatball Products Due To Possible Foreign Matter Contamination",
        //"FSIS Issues Public Health Alert for Beef and Pork Products  Due to Misbranding and Undeclared Allergen",
        String companyName = null;
        if (fsisRecallDto.establishment() != null && !fsisRecallDto.establishment().isEmpty()) {
            companyName = String.join(", ", fsisRecallDto.establishment());
        }

        if (fsisRecallDto.title() != null && !fsisRecallDto.title().isEmpty()) {
            String title = fsisRecallDto.title().toUpperCase();
            if (title.contains("RECALLS")) {
                companyName = StringUtils.substringBefore(title, "RECALLS");
            }
        }

        return ProductDescription.builder()
                .companyName(companyName)
                .productName(String.join(", ", fsisRecallDto.productItems()))
                .identifiers(fsisRecallDto.productItems())
                .build();
    }

    private <T> List<FoodRecall> safeMapToDto(List<T> dtoList,
                                                     Function<T, FoodRecall> mapper,
                                                     String agencyName) {
        return Optional.ofNullable(dtoList).orElse(List.of())
                .stream()
                .map(dto -> {
                    try {
                        return mapper.apply(dto);
                    } catch (RuntimeException ex) {
                        log.error("Failed to map {} recall record. Skipping item.", agencyName, ex);
                        return null;
                    }})
                .filter(Objects::nonNull)
                .toList();
    }
}
