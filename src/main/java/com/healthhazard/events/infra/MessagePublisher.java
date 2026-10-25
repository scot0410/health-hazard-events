package com.healthhazard.events.infra;

import com.healthhazard.events.model.dto.FoodRecall;

import java.util.List;

public sealed interface MessagePublisher permits RecallEventPublisher {
    void publishEvents(List<FoodRecall> recalls);
}
