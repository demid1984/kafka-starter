package com.zdemid.kafka.conditional;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Conditional(OnJsonValueDeserializer.class)
public @interface ConditionalOnJsonValueDeserializer {
}
