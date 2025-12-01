package org.example.scheduler;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import org.example.dto.Event;
import org.example.entity.Outbox;
import org.example.entity.OutboxStatus;
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
    ProductProducer productProducer;

    @Scheduled(every="30s")
    public void publishPendingOutbox() {
        System.out.println("executing scheduled task!");
        List<Outbox> pendingOutboxes = outboxRepository.list("status", OutboxStatus.PENDING);
        System.out.println(pendingOutboxes.size());
        pendingOutboxes.forEach((outbox)->{
            productProducer.send(jsonb.fromJson(outbox.getEvent(), Event.class))
                .whenComplete((v, ex) -> {
                    if (ex != null) {
                        System.out.println("sending Failed on attempt: " + outbox.getAttempts());
                        outbox.setAttempts(outbox.getAttempts() + 1);
                        if(outbox.getAttempts() >= 5) {
                            outbox.setStatus(OutboxStatus.FAILED);
                            System.out.println("Abandoning Action, Marking As failing!");
                        }
                    }
                    else {
                        outbox.setStatus(OutboxStatus.SUCCEEDED);
                        System.out.println("sending Completed!");
                    }
                    outbox.persist();
                });
        });

    }
}