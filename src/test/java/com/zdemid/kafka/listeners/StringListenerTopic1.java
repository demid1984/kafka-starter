package com.zdemid.kafka.listeners;

import lombok.Getter;
import org.springframework.kafka.annotation.KafkaListener;

@Getter
public class StringListenerTopic1 {

    private String message;

    @KafkaListener(topics = "${test.kafka.topic1}", groupId = "test")
    public void listener(String message) {
        this.message = message;
    }
}
