package com.zdemid.kafka.conditional;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

public class OnKafkaEnabledCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return StringUtils.hasLength(context.getEnvironment().getProperty("spring.kafka.bootstrap-servers")) ||
                StringUtils.hasLength(context.getEnvironment().getProperty("spring.kafka.producer.bootstrap-servers"));
    }
}
