package com.zdemid.kafka.consumer;

import com.zdemid.kafka.configuration.KafkaConfiguration;
import com.zdemid.kafka.listeners.ConsumerStartedEventListener;
import com.zdemid.kafka.listeners.JsonClassListenerTopic2;
import com.zdemid.kafka.listeners.JsonListenerTopic1;
import com.zdemid.kafka.model.TestModel;
import com.zdemid.kafka.model.TestModel2;
import com.zdemid.kafka.utils.KafkaUtils;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(SpringRunner.class)
@Testcontainers
@SpringBootTest
@DirtiesContext
public class KafkaStarterConsumerJsonSerializationTests {

    @ClassRule
    public static final KafkaContainer kafkaContainer = KafkaUtils.createKafka();
    static {
        kafkaContainer.start();
        KafkaUtils.createKafkaTopic(kafkaContainer.getHost(), kafkaContainer.getFirstMappedPort(), KafkaUtils.KAFKA_TOPIC);
        KafkaUtils.createKafkaTopic(kafkaContainer.getHost(), kafkaContainer.getFirstMappedPort(), KafkaUtils.KAFKA_TOPIC2);
    }

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.producer.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.producer.value-serializer", () -> "org.springframework.kafka.support.serializer.JsonSerializer");
        registry.add("spring.kafka.consumer.value-deserializer", () -> "org.springframework.kafka.support.serializer.JsonDeserializer");
    }

    @TestConfiguration
    public static class Configuration {
        @Bean
        public JsonListenerTopic1 listenerTopic1() {
            return new JsonListenerTopic1();
        }

        @Bean
        public JsonClassListenerTopic2 listenerTopic2() {
            return new JsonClassListenerTopic2();
        }

    }

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private JsonListenerTopic1 listenerTopic1;

    @Autowired
    private JsonClassListenerTopic2 listenerTopic2;

    @Autowired
    private ConsumerStartedEventListener consumerStartedEventListener;

    @Test
    public void contextLoads() throws Exception {
        assertNotNull(applicationContext.getBean(KafkaConfiguration.class));
        Awaitility.await().atMost(Duration.ofSeconds(3)).until(() -> consumerStartedEventListener.isStarted());

        var model = new TestModel();
        model.setId(123);
        model.setData("qwerty message");

        var model2 = new TestModel2();
        model2.setId(123);
        model2.setMessage("qwerty message");
        model2.setCount(3);

        kafkaTemplate.send(KafkaUtils.KAFKA_TOPIC, model);
        kafkaTemplate.send(KafkaUtils.KAFKA_TOPIC2, model);

        Awaitility.await().atMost(Duration.ofSeconds(3)).until(() -> listenerTopic1.getMessage() != null);
        Awaitility.await().atMost(Duration.ofSeconds(3)).until(() -> listenerTopic2.getMessage() != null);
        assertEquals(model, listenerTopic1.getMessage());
        assertEquals(model, listenerTopic2.getMessage());
    }
}
