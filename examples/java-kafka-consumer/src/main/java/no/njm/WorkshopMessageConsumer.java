package no.njm;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@Component
class WorkshopMessageProducer {

    public static final Logger log = LoggerFactory.getLogger(WorkshopMessageProducer.class);

    private final KafkaProducer<String, WorkshopMessage> kafkaProducer;

    @Autowired
    public WorkshopMessageProducer(KafkaProducer<String, WorkshopMessage> workshopMessageKafkaProducer) {
        kafkaProducer = workshopMessageKafkaProducer;
    }

    public void sendSync(WorkshopMessage workshopMessage) {
        try {
            Future<RecordMetadata> send = kafkaProducer.send(new ProducerRecord<>("workshop.messages", workshopMessage.id, workshopMessage));
            kafkaProducer.flush();
            // .get() waits for the producer to flush.
            RecordMetadata recordMetadata = send.get();
            log.info("Produced record with key: [{}] on topic: [{}].", workshopMessage.id, recordMetadata.topic());
        } catch (Exception e) {
            log.error("Error sending message: {}.", workshopMessage);
            throw new WorkshopMessageException(e);
        }
    }

    public void sendAsync(WorkshopMessage workshopMessage) {
        kafkaProducer.send(new ProducerRecord<>("workshop.messages", workshopMessage.id, workshopMessage), (recordMetadata, e) -> {
            if (e != null) {
                log.error("Error sending async message: {}.", workshopMessage);
            }
            log.info("Produced async record with key: [{}] on topic: [{}].", workshopMessage.id, recordMetadata.topic());
        });

        // Sends all buffered records immediately, even if linger.ms is greater than 0.
        kafkaProducer.flush();
    }

    static class WorkshopMessageException extends RuntimeException {

        WorkshopMessageException(Throwable cause) {
            super(cause);
        }
    }

    record WorkshopMessage(String id, String message) {
    }
}
