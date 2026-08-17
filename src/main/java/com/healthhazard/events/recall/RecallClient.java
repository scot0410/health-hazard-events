package com.healthhazard.events.recall;

import com.healthhazard.events.client.FdaHttpClient;
import com.healthhazard.events.client.FsisHttpClient;
import com.healthhazard.events.model.FoodRecall;
import com.healthhazard.events.model.ProductDescription;
import com.healthhazard.events.model.dto.FdaRecallDto;
import com.healthhazard.events.model.dto.FdaRecallResponseDto;
import com.healthhazard.events.model.dto.FsisRecallDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecallClient implements ExternalApiClient {
    private final FdaHttpClient fdaHttpClient;
    private final FsisHttpClient fsisHttpClient;

    @Override
    public List<FoodRecall> fetchLatestRecalls() {
        var fdaRecallData = Optional.ofNullable(fdaHttpClient.fetchOngoingRecallData())
                .orElseGet(() -> FdaRecallResponseDto.builder().build());

        var fsisRecallData = Optional.ofNullable(fsisHttpClient.fetchRecallData())
                .orElseGet(() -> List.of(FsisRecallDto.builder().build()));

        return buildFoodRecall(fdaRecallData.results(), fsisRecallData);
    }

    private static List<FoodRecall> buildFoodRecall(List<FdaRecallDto> fdaRecallDtos,
                                                    List<FsisRecallDto> fsisRecallDtos) {

        var fdaRecalls = fdaRecallDtos.stream().map(RecallClient::fdaRecallMapper).toList();
        var fsisRecalls = fsisRecallDtos.stream().map(RecallClient::fsisRecallMapper).toList();
        return Stream.concat(fdaRecalls.stream(), fsisRecalls.stream()).toList();
    }

    private static FoodRecall fdaRecallMapper(FdaRecallDto fdaRecallDto) {
        var reportDate = fdaRecallDto.reportDate();
        var parsedDate = reportDate != null
                ? LocalDate.parse(reportDate, DateTimeFormatter.ofPattern("yyyyMMdd"))
                : null;
        var simpleLocation = String.format("%s, %s, %s", fdaRecallDto.city(), fdaRecallDto.state(), fdaRecallDto.country());

        var productDescription = buildProductDescription(fdaRecallDto);
        return FoodRecall.builder()
                .recallNumber(fdaRecallDto.recallNumber())
                .recallingFirm(fdaRecallDto.recallingFirm())
                .productDescription(productDescription)
                .reasonForRecall(fdaRecallDto.reasonForRecall())
                .classification(fdaRecallDto.classification())
                .status(fdaRecallDto.status())
                .parsedDate(parsedDate)
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
        var simpleLocation = String.join(", ", fsisRecallDto.states());
        var reason = String.join(", ", fsisRecallDto.recallReason());

        var productDescription = collectProductDescription(fsisRecallDto);
        return FoodRecall.builder()
                .recallNumber(fsisRecallDto.recallNumber())
                .recallingFirm(fsisRecallDto.mediaContact())
                .productDescription(productDescription)
                .reasonForRecall(reason)
                .classification(fsisRecallDto.riskLevel())
                .status(fsisRecallDto.recallType())
                .parsedDate(LocalDate.parse(fsisRecallDto.recallDate()))
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
}
