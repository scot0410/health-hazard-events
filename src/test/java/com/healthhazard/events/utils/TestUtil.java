package com.healthhazard.events.utils;

import com.healthhazard.events.model.FoodRecall;
import com.healthhazard.events.model.ProductDescription;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@UtilityClass
public class TestUtil {
    public static FoodRecall getFoodRecall() {
        var identifiers = List.of(
                "Ellsworth Sour Cream + Onion Seasoning, Part #:  180000113.  Net Wt 25.00 lbs (11.3kg).  PS Seasoning & Spices 216 W Pleasant St Iron Ridge WI  53035",
                "Lot #: 2026-51127"
        );

        var productDescription = ProductDescription.builder()
                .companyName("PS Seasoning & Spices, Inc.")
                .productName("Ellsworth Sour Cream + Onion Seasoning")
                .identifiers(identifiers)
                .build();

        return FoodRecall.builder()
                .recallingFirm("FDA")
                .recallNumber("F-1234-2026")
                .productDescription(productDescription)
                .recallReason("Product was made with milk powder recalled by the supplier due to Salmonella contamination.")
                .classification("Class I")
                .status("Ongoing")
                .parsedDate(LocalDate.of(2026, Month.MAY, 27))
                .distLocation("Iron Ridge, WI, United States")
                .sourceAgency("FDA")
                .infoUrl(null)
                .build();
    }
    public static FoodRecall getFoodRecallWithRecallNumber(String recallNumber) {
        var identifiers = List.of(
                "Ellsworth Sour Cream + Onion Seasoning, Part #:  180000113.  Net Wt 25.00 lbs (11.3kg).  PS Seasoning & Spices 216 W Pleasant St Iron Ridge WI  53035",
                "Lot #: 2026-51127"
        );

        var productDescription = ProductDescription.builder()
                .companyName("PS Seasoning & Spices, Inc.")
                .productName("Ellsworth Sour Cream + Onion Seasoning")
                .identifiers(identifiers)
                .build();

        return FoodRecall.builder()
                .recallingFirm("FDA")
                .recallNumber(recallNumber)
                .productDescription(productDescription)
                .recallReason("Product was made with milk powder recalled by the supplier due to Salmonella contamination.")
                .classification("Class I")
                .status("Ongoing")
                .parsedDate(LocalDate.of(2026, Month.MAY, 27))
                .distLocation("Iron Ridge, WI, United States")
                .sourceAgency("FDA")
                .infoUrl(null)
                .build();
    }
}
