package com.zdemid.kafka.component;

import com.zdemid.kafka.conditional.KafkaEnabledConditional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@KafkaEnabledConditional
@Component
@RequiredArgsConstructor
public class KafkaListenerConfigurerComponent implements KafkaListenerConfigurer {

    private final LocalValidatorFactoryBean validatorFactory;

    @Override
    public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
        registrar.setValidator(validatorFactory);
    }

}
