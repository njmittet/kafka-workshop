package no.njm

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka

@Configuration
@EnableKafka
class KafkaConfiguration(
    @Value("\${KAFKA_BROKERS}") private val kafkaBrokers: String,
) {
    @Bean
    fun workshopMessageKafkaProducer(): KafkaProducer<String, WorkshopMessage> {
        val kafkaConfig = mapOf(
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JacksonKafkaSerializer::class.java,
            ProducerConfig.ACKS_CONFIG to "all",
            ProducerConfig.RETRIES_CONFIG to 10,
            ProducerConfig.RETRY_BACKOFF_MS_CONFIG to 100,
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaBrokers
        )
        return KafkaProducer<String, WorkshopMessage>(kafkaConfig)
    }
}
