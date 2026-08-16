package com.healthhazard.events.infra;

@FunctionalInterface
public interface MessagePublisher {
    void publish(Object message);
}
