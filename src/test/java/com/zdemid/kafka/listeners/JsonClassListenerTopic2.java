package com.zdemid.kafka.listeners;

import com.zdemid.kafka.model.TestModel;
import com.zdemid.kafka.model.TestModel2;
import lombok.Getter;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;

@Getter
@KafkaListener(topics = "${test.kafka.topic2}", groupId = "test")
public class JsonClassListenerTopic2 {

    private TestModel message;
    private TestModel2 message2;

    @KafkaHandler
    public void listener(@Payload TestModel message) {
        this.message = message;
    }

    @KafkaHandler
    public void listener2(TestModel2 message) {
        this.message2 = message;
    }

}
