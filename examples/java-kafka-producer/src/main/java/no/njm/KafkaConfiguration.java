package no.njm;

import no.njm.WorkshopMessageProducer.WorkshopMessage;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KafkaConfiguration {

    private final String kafkaBrokers;

    public KafkaConfiguration(@Value("${KAFKA_BROKERS}") String kafkaBrokers) {
        this.kafkaBrokers = kafkaBrokers;
    }

    @Bean
    public KafkaProducer<String, WorkshopMessage> workshopMessageKafkaProducer() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokers);
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.RETRIES_CONFIG, 10);
        properties.put(ProducerConfig.LINGER_MS_CONFIG, 5000);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaJsonSerializer");

        return new KafkaProducer<>(properties);
    }
}
