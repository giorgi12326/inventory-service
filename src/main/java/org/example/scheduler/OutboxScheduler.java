package org.example.scheduler;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.transaction.Transactional;
import org.example.dto.Event;
import org.example.entity.Outbox;
import org.example.entity.OutboxStatus;
import org.example.outbox.EventHandler;
import org.example.outbox.HandlerRegistry;
import org.example.outbox.ProductReservedHandler;
import org.example.repository.OutboxRepository;
import org.example.service.ProductProducer;

import java.util.List;

@ApplicationScoped
public class OutboxScheduler {
    @Inject
    Jsonb jsonb;

    @Inject
    OutboxRepository outboxRepository;

    @Inject
    OutboxScheduler self;

    @Inject
    HandlerRegistry handlerRegistry;

    @Scheduled(every="30s")
    public void publishPendingOutbox() {
        System.out.println("executing scheduled task!");
        List<Outbox> pendingOutboxes = outboxRepository.list("status", OutboxStatus.PENDING);
        System.out.println(pendingOutboxes.size());
        pendingOutboxes.forEach((outbox)->{
            Event event = jsonb.fromJson(outbox.getEvent(), Event.class);
            EventHandler handler = handlerRegistry.getHandler(outbox.getEvent());

            handler.process(event).whenComplete((v, ex) -> {
                if (ex != null) {
                    System.out.println("sending Failed on attempt: " + outbox.getAttempts());
                    self.incrementAttempts(outbox);
                    if(outbox.getAttempts() >= 5) {
                        self.markAsFailed(outbox);
                        handler.compensate(event);
                        System.out.println("Abandoning Action, Marking As failing!");
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