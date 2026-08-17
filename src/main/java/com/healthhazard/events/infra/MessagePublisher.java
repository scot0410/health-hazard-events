package com.healthhazard.events.infra;

import com.healthhazard.events.model.FoodRecall;

import java.util.List;

@FunctionalInterface
public interface MessagePublisher {
    void publish(List<FoodRecall> recalls);
}
