package com.zdemid.kafka.listeners;

import lombok.Getter;
import org.springframework.kafka.annotation.KafkaListener;

@Getter
public class LongListenerTopic2 {

    private Long code;

    @KafkaListener(topics = "${test.kafka.topic2}", groupId = "test")
    public void listener(Long code) {
        this.code = code;
    }
}
