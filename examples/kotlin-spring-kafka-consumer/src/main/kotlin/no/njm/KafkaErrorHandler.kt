package no.njm

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.kafka.listener.MessageListenerContainer
import org.springframework.stereotype.Component
import org.springframework.util.backoff.ExponentialBackOff

@Component
class KafkaErrorHandler : DefaultErrorHandler(
    // Creating a custom exponential backoff strategy.
    ExponentialBackOff(1000L, 2.0).apply {
        maxInterval = 32_000L
    }
) {

    val log = getLogger()

    override fun handleRemaining(
        thrownException: Exception,
        records: List<ConsumerRecord<*, *>>,
        consumer: Consumer<*, *>,
        container: MessageListenerContainer
    ) {
        if (records.isEmpty()) {
            log.error("The listener returned an empty set of records.")
        }
        records.forEach { record ->
            log.error(
                "Error processing record with key: [${record.key()}] (offset: ${record.offset()}, partition: ${record.partition()}).",
            )
        }
        // The parent error handler will log the exception.
        super.handleRemaining(thrownException, records, consumer, container)
    }

    override fun handleBatch(
        thrownException: java.lang.Exception,
        records: ConsumerRecords<*, *>,
        consumer: Consumer<*, *>,
        container: MessageListenerContainer,
        invokeListener: Runnable
    ) {
        if (records.isEmpty) {
            log.error("The listener returned an empty set of records.")
        }
        records.forEach { record ->
            log.error(
                "Error processing record with key: [${record.key()}] (offset: ${record.offset()}, partition: ${record.partition()}).",
            )
        }
        // The parent error handler will log the exception.
        super.handleBatch(thrownException, records, consumer, container, invokeListener)
    }
}
