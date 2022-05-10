package com.zdemid.kafka.configuration;

import com.zdemid.kafka.component.JsonValueDeserializer;
import com.zdemid.kafka.conditional.ConditionalOnJsonValueDeserializer;
import com.zdemid.kafka.conditional.KafkaEnabledConditional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@KafkaEnabledConditional
@Configuration
@RequiredArgsConstructor
public class KafkaConfiguration {

    @Bean
    @ConditionalOnJsonValueDeserializer
    public ConsumerFactory<Object, Object> kafkaConsumerFactory(KafkaProperties kafkaProperties,
                                                                JsonValueDeserializer jsonValueDeserializer) {
        var consumerFactory = new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
        consumerFactory.setValueDeserializer(new ErrorHandlingDeserializer<>(jsonValueDeserializer));
        return consumerFactory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
                                                                                                 ConsumerFactory<Object, Object> kafkaConsumerFactory,
                                                                                                 CommonErrorHandler commonErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<Object, Object> listenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
        listenerContainerFactory.setCommonErrorHandler(commonErrorHandler);
        configurer.configure(listenerContainerFactory, kafkaConsumerFactory);
        return listenerContainerFactory;
    }

    @Bean
    public MessageHandlerMethodFactory kafkaHandlerMethodFactory(LocalValidatorFactoryBean validatorFactory) {
        var messageHandlerMethodFactory = new DefaultMessageHandlerMethodFactory();
        messageHandlerMethodFactory.setValidator(validatorFactory);
        return messageHandlerMethodFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public CommonErrorHandler commonErrorHandler() {
        var backoff = new FixedBackOff(2500, 500);
        return new DefaultErrorHandler(backoff);
    }

}
