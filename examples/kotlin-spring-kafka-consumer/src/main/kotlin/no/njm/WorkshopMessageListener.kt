package no.njm

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class WorkshopMessageListener {

    private val log = getLogger()

    @KafkaListener(
        topics = [WORKSHOP_TOPIC],
        groupId = "kotlin-consumer-concurrent",
        properties = ["auto.offset.reset=earliest"],
        concurrency = "3",
    )
    fun listen(consumerRecord: ConsumerRecord<String, String>, acknowledgment: Acknowledgment) {
        try {
            val message = consumerRecord.value().toWorkshopMessage()
            log.info(
                "Message with key:[${consumerRecord.key()}] on partition:[${consumerRecord.partition()}] is: $message."
            )
            acknowledgment.acknowledge()
        } catch (e: Exception) {
            log.error("Error processing message with key: [${consumerRecord.key()}].")
            // Make sure the error is picked up by the KafkaErrorHandler.
            throw e
        }
    }
}
