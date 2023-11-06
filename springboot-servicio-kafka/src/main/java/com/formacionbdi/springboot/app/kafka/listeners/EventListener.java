package com.formacionbdi.springboot.app.kafka.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventListener {

    private static Logger log = LoggerFactory.getLogger(EventListener.class);
    @KafkaListener(topics = "product-topic")
    public void handleOrdersNotifications(String message) {
        log.info("JSON received:" + message);
    }
}
