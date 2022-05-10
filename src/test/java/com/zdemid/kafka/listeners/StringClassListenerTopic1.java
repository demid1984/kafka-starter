package com.zdemid.kafka.listeners;

import lombok.Getter;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;

@Getter
@KafkaListener(topics = "${test.kafka.topic1}", groupId = "test")
public class StringClassListenerTopic1 {

    private String message;

    @KafkaHandler
    public void listener(String message) {
        this.message = message;
    }
}
