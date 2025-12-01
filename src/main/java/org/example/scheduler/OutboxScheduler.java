package org.example.scheduler;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.entity.Outbox;
import org.example.entity.OutboxStatus;
import org.example.repository.OutboxRepository;
import org.example.service.ProductProducer;

import java.util.List;

@ApplicationScoped
public class OutboxScheduler {
    @Inject
    OutboxRepository outboxRepository;

    @Inject
    ProductProducer productProducer;

    @Scheduled(every="30sec")
    public void publishPendingOutbox() {
        List<Outbox> pendingOutboxes = outboxRepository.list("status", OutboxStatus.PENDING);
        pendingOutboxes.forEach((outbox)->{
            productProducer.send(outbox.getEvent())
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