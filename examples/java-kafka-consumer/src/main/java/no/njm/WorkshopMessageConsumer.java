package no.njm;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

import static no.njm.Application.WORKSHOP_TOPIC;

@Component
class WorkshopMessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(WorkshopMessageConsumer.class);

    private final KafkaConsumer<String, WorkshopMessage> kafkaConsumer;

    @Autowired
    public WorkshopMessageConsumer(KafkaConsumer<String, WorkshopMessage> workshopMessageKafkaConsumer) {
        kafkaConsumer = workshopMessageKafkaConsumer;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        kafkaConsumer.subscribe(List.of(WORKSHOP_TOPIC));

        try {
            while (true) {
                // Returns immediately if there are records available. Otherwise, it will await the timeout value.
                ConsumerRecords<String, WorkshopMessage> consumerRecords = kafkaConsumer.poll(Duration.ofSeconds(10));

                consumerRecords.forEach(consumerRecord -> {
                    String key = consumerRecord.key();
                    int partition = consumerRecord.partition();
                    WorkshopMessage workshopMessage = consumerRecord.value();

                    log.info("Message with key:[{}] on partition:[{}] is: {}.", key, partition, workshopMessage);
                    // Commit each message immediately.
                    kafkaConsumer.commitSync();
                });
            }
        } finally {
            kafkaConsumer.close();
        }
    }

    record WorkshopMessage(String id, String message) {
    }
}
