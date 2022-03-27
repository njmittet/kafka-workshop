package no.njm

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component

@Component
class WorkshopMessageProducer(
    private val workshopMessageKafkaProducer: KafkaProducer<String, WorkshopMessage>
) {

    private val log = getLogger()

    fun sendSync(workshopMessage: WorkshopMessage) {
        try {
            val recordMetadata = workshopMessageKafkaProducer.send(
                ProducerRecord(WORKSHOP_TOPIC, workshopMessage.id, workshopMessage)
                // Blocking wait for broker acknowledgement.
            ).get()
            log.info("Produced record with key: [${workshopMessage.id}] on topic: [${recordMetadata.topic()}].")
        } catch (e: Exception) {
            log.error("Error sending message: $workshopMessage.")
            // Future.get() throws either a ExecutionException or a TimeoutException. Both are checked exceptions.
            throw WorkshopMessageException(e)
        }
    }

    fun sendAsync(workshopMessage: WorkshopMessage) {
        workshopMessageKafkaProducer.send(
            ProducerRecord(WORKSHOP_TOPIC, workshopMessage.id, workshopMessage)
        ) { recordMetadata, e ->
            if (e != null) {
                log.error("Error sending async message: $workshopMessage.", e)
            }
            log.info("Produced async record with key: [${workshopMessage.id}] on topic: [${recordMetadata.topic()}].")
        }
    }

    class WorkshopMessageException(e: Exception) : RuntimeException(e)
}

data class WorkshopMessage(val id: String, val message: String)
