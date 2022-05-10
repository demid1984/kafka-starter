package com.zdemid.kafka.conditional;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Conditional({OnKafkaEnabledCondition.class})
public @interface KafkaEnabledConditional {
}
