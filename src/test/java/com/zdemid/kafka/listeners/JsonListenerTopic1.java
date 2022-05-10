package com.zdemid.kafka.listeners;

import com.zdemid.kafka.model.TestModel;
import lombok.Getter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

@Getter
public class JsonListenerTopic1 {

    private TestModel message;

    @KafkaListener(topics = "${test.kafka.topic1}", groupId = "test")
    public void listener(@Payload TestModel message) {
        this.message = message;
    }
}
