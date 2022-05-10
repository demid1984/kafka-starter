package com.zdemid.kafka.consumer;

import com.zdemid.kafka.configuration.KafkaConfiguration;
import com.zdemid.kafka.listeners.ConsumerStartedEventListener;
import com.zdemid.kafka.listeners.LongListenerTopic2;
import com.zdemid.kafka.listeners.StringListenerTopic1;
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
public class KafkaStarterConsumerDefaultSerializationTests {

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
	}

	@TestConfiguration
	public static class Configuration {
		@Bean
		public StringListenerTopic1 listenerTopic1() {
			return new StringListenerTopic1();
		}

		@Bean
		public LongListenerTopic2 longListenerTopic2() {
			return new LongListenerTopic2();
		}
	}

	@Autowired
	private ConfigurableApplicationContext applicationContext;

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Autowired
	private StringListenerTopic1 listenerTopic1;

	@Autowired
	private LongListenerTopic2 listenerTopic2;

	@Autowired
	private ConsumerStartedEventListener consumerStartedEventListener;

	@Test
	public void contextLoads() {
		assertNotNull(applicationContext.getBean(KafkaConfiguration.class));
		Awaitility.await().atMost(Duration.ofSeconds(3)).until(() -> consumerStartedEventListener.isStarted());

		var message = "test info";
		var code = 12345L;

		kafkaTemplate.send(KafkaUtils.KAFKA_TOPIC, message);
		kafkaTemplate.send(KafkaUtils.KAFKA_TOPIC2, String.valueOf(code));

		Awaitility.await().atMost(Duration.ofSeconds(3)).until(() -> listenerTopic1.getMessage() != null);
		assertEquals(message, listenerTopic1.getMessage());
		assertEquals(code, listenerTopic2.getCode());
	}

}
