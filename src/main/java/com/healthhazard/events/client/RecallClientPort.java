package com.healthhazard.events.client;

import com.healthhazard.events.model.FoodRecall;

import java.util.List;

public interface RecallClientPort {
    List<FoodRecall> fetchLatestRecalls();
}
