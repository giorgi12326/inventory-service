package org.example.scheduler;

import io.quarkus.arc.properties.IfBuildProperty;
import io.quarkus.kafka.client.serialization.JsonbSerializer;
import io.quarkus.scheduler.Scheduled;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.transaction.Transactional;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.dto.Event;
import org.example.entity.Outbox;
import org.example.entity.OutboxStatus;
import org.example.repository.OutboxRepository;

import java.util.List;
import java.util.Properties;

@ApplicationScoped
@IfBuildProperty(name = "kafka.enabled", stringValue = "true")
public class OutboxScheduler {
    @Inject
    Jsonb jsonb;

    @Inject
    OutboxRepository outboxRepository;

    @Inject
    OutboxScheduler self;

    @Inject
    KafkaProducer<String, Event> kafkaProducer;

    @Scheduled(every="30s")
    public void publishPendingOutbox() {
        System.out.println("executing scheduled task!");
        List<Outbox> pendingOutboxes = outboxRepository.list("status", OutboxStatus.PENDING);
        System.out.println(pendingOutboxes.size());
        pendingOutboxes.forEach((outbox)->{
            Event event = jsonb.fromJson(outbox.getEvent(), Event.class);
            ProducerRecord<String, Event> record = new ProducerRecord<>(outbox.getDestination(), event);

            kafkaProducer.send(record,(v, ex) -> {
                if (ex != null) {
                    System.out.println("sending Failed on attempt: " + outbox.getAttempts());
                    self.incrementAttempts(outbox);
                    if(outbox.getAttempts() >= 5) {
                        self.markAsFailed(outbox);
                    }
                }
                else {
                    self.markAsSucceeded(outbox);
                    System.out.println("sending Completed!");
                }
            });

        });

    }

    @Transactional
    void markAsSucceeded(Outbox outbox) {
        Outbox managed = outboxRepository.findById(outbox.getId());

        managed.setStatus(OutboxStatus.SUCCEEDED);
        managed.persist();
    }

    @Transactional
    void markAsFailed(Outbox outbox) {
        Outbox managed = outboxRepository.findById(outbox.getId());
        managed.setStatus(OutboxStatus.FAILED);
        managed.persist();
    }

    @Transactional
    void incrementAttempts(Outbox outbox) {
        Outbox managed = outboxRepository.findById(outbox.getId());
        managed.setAttempts(managed.getAttempts() + 1);
        managed.persist();
    }
}