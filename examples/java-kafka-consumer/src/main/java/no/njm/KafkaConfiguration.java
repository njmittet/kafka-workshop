package no.njm;

import io.confluent.kafka.serializers.KafkaJsonDeserializerConfig;
import no.njm.WorkshopMessageConsumer.WorkshopMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaConfiguration {

    private final String kafkaBrokers;

    KafkaConfiguration(@Value("${spring.kafka.bootstrap-servers}") String kafkaBrokers) {
        this.kafkaBrokers = kafkaBrokers;
    }

    @Bean
    KafkaConsumer<String, WorkshopMessage> workshopMessageKafkaConsumer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaJsonDeserializer");
        properties.put(KafkaJsonDeserializerConfig.JSON_VALUE_TYPE, WorkshopMessage.class);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "java-consumer");
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 5);

        return new KafkaConsumer<>(properties);
    }
}
