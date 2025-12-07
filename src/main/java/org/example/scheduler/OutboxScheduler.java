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

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-0.kafka:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, org.apache.kafka.common.serialization.StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonbSerializer.class.getName());

        this.kafkaProducer = new KafkaProducer<>(props);
    }

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
        outbox.setStatus(OutboxStatus.SUCCEEDED);
        outbox.persist();
    }

    @Transactional
    void markAsFailed(Outbox outbox) {
        outbox.setStatus(OutboxStatus.FAILED);
        outbox.persist();
    }

    @Transactional
    void incrementAttempts(Outbox outbox) {
        outbox.setAttempts(outbox.getAttempts() + 1);
        outbox.persist();
    }
}