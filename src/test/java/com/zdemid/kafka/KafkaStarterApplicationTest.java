package com.zdemid.kafka;

import com.zdemid.kafka.utils.KafkaUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;

@SpringBootApplication
public class KafkaStarterApplicationTest implements EnvironmentPostProcessor {

    public static void main(String... args) {
        SpringApplication.run(KafkaStarterApplicationTest.class, args);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        environment.getSystemProperties().put("test.kafka.topic1", KafkaUtils.KAFKA_TOPIC);
        environment.getSystemProperties().put("test.kafka.topic2", KafkaUtils.KAFKA_TOPIC2);
    }
}
