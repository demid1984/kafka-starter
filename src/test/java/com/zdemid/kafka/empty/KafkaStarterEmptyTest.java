package com.zdemid.kafka.empty;

import com.zdemid.kafka.configuration.KafkaConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class KafkaStarterEmptyTest {

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Test
    void contextLoads() {
        assertThrows(NoSuchBeanDefinitionException.class, () -> applicationContext.getBean(KafkaConfiguration.class));
    }
}
