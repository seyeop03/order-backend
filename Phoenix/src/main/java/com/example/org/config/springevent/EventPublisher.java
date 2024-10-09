package com.example.org.config.springevent;

import org.springframework.context.ApplicationEventPublisher;

public class EventPublisher {

    private static ApplicationEventPublisher publisher;

    static void setPublisher(ApplicationEventPublisher publisher) {
        EventPublisher.publisher = publisher;
    }

    public static void publish(Object event) {
        if (publisher != null) {
            publisher.publishEvent(event);
        }
    }
}
