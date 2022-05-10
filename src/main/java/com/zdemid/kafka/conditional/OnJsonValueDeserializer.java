package com.zdemid.kafka.conditional;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.kafka.support.serializer.JsonDeserializer;

public class OnJsonValueDeserializer implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return JsonDeserializer.class.getCanonicalName().equals(context.getEnvironment().getProperty("spring.kafka.consumer.value-deserializer"));
    }
}
