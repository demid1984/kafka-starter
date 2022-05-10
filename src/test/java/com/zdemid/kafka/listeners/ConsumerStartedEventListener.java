package com.zdemid.kafka.listeners;

import lombok.Getter;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.event.ConsumerStartingEvent;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ConsumerStartedEventListener {

    private boolean started;

    @EventListener
    public void eventHandler(ConsumerStartingEvent event) {
        this.started = true;
    }
}
