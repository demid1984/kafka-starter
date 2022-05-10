package com.zdemid.kafka.component;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.zdemid.kafka.conditional.ConditionalOnJsonValueDeserializer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@ConditionalOnJsonValueDeserializer
@RequiredArgsConstructor
public class PostJsonValueDeserializerApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private final ConfigurableListableBeanFactory beanFactory;
    private final JsonValueDeserializer jsonValueDeserializer;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        var context = event.getApplicationContext();
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            var beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            Class<?> origCls;
            try {
                var origBeanClassName = beanDefinition.getBeanClassName();
                if (origBeanClassName == null) {
                    origCls = context.getBean(beanDefinitionName).getClass();
                } else {
                    origCls = Class.forName(origBeanClassName);
                }
                getKafkaListeners(origCls).forEach(e -> e.getValue().forEach(type -> jsonValueDeserializer.add(e.getKey(), type)));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<Map.Entry<String, List<JavaType>>> getKafkaListeners(Class<?> beanCls) throws BeansException {
        if (beanCls.isAnnotationPresent(KafkaListener.class)) {
            var kafkaTopics = beanCls.getAnnotation(KafkaListener.class).topics();
            List<JavaType> topicTypes = Stream.of(ReflectionUtils.getDeclaredMethods(beanCls))
                    .filter(m -> m.isAnnotationPresent(KafkaHandler.class))
                    .map(this::getKafkaMessageType)
                    .collect(Collectors.toList());
            return Stream.of(kafkaTopics)
                    .map(t -> Map.entry(beanFactory.resolveEmbeddedValue(t), topicTypes))
                    .collect(Collectors.toList());
        } else {
            return Stream.of(ReflectionUtils.getDeclaredMethods(beanCls))
                    .filter(m -> m.isAnnotationPresent(KafkaListener.class))
                    .flatMap(m -> {
                        var kafkaTopics = m.getAnnotation(KafkaListener.class).topics();
                        var kafkaMessageTypes = getKafkaMessageType(m);
                        return Stream.of(kafkaTopics)
                                .map(t -> Map.entry(beanFactory.resolveEmbeddedValue(t), List.of(kafkaMessageTypes)));
                    }).collect(Collectors.toList());
        }
    }

    private JavaType getKafkaMessageType(Method method) {
        var parameters = method.getParameters();
        Class<?> kafkaMessageCls;
        if (parameters.length == 1) {
            kafkaMessageCls = parameters[0].getType();
        } else {
            kafkaMessageCls = Stream.of(method.getParameters())
                    .filter(p -> p.isAnnotationPresent(Payload.class))
                    .findFirst()
                    .map(Parameter::getType)
                    .orElseThrow(() -> new IllegalStateException("Cannot find @Payload annotation in kafka listener"));
        }
        return TypeFactory.defaultInstance().constructType(kafkaMessageCls);
    }
}
