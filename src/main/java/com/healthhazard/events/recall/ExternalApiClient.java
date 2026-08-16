package com.healthhazard.events.recall;

import com.healthhazard.events.model.FoodRecall;

import java.util.List;

public interface ExternalApiClient {
    List<FoodRecall> fetchLatestRecalls();
}
