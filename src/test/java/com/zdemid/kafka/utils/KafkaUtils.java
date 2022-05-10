package com.zdemid.kafka.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KafkaUtils {

    public static final String KAFKA_TOPIC = "test_topic";
    public static final String KAFKA_TOPIC2 = "test_topic_2";

    public static void createKafkaTopic(String host, int port, String kafkaTopic) {
        createKafkaTopic(host, port, kafkaTopic, 1);
    }

    public static void createKafkaTopic(String host, int port, String kafkaTopic, int numPartitions) {
        var kafkaBrokerString = String.format("%s:%d", host, port);
        try (var admin = AdminClient.create(Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokerString))) {
            admin.createTopics(List.of(new NewTopic(kafkaTopic, numPartitions, (short) 1)));
        }
    }

    public static KafkaContainer createKafka() {
        var kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka").withTag("5.4.3"))
                .withEmbeddedZookeeper()
                .withExposedPorts(9093);
        kafkaContainer.start();
        return kafkaContainer;
    }

}
