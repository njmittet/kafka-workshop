package no.njm

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties.AckMode

@Configuration
@EnableKafka
class KafkaConfiguration {

    @Bean
    fun consumerFactory(kafkaProperties: KafkaProperties) =
        DefaultKafkaConsumerFactory<String, String>(kafkaProperties.buildConsumerProperties() + consumerConfig)

    val consumerConfig = mapOf(
        ConsumerConfig.GROUP_ID_CONFIG to "kotlin-consumer",
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "latest",
        ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
        ConsumerConfig.MAX_POLL_RECORDS_CONFIG to 50,

    )

    @Bean
    fun kafkaListenerContainerFactory(
        consumerFactory: ConsumerFactory<String, String>,
        errorHandler: KafkaErrorHandler
    ) = ConcurrentKafkaListenerContainerFactory<String, String>().also {
        it.consumerFactory = consumerFactory
        it.containerProperties.ackMode = AckMode.MANUAL_IMMEDIATE
        it.isBatchListener = true
        it.setCommonErrorHandler(errorHandler)
    }
}
