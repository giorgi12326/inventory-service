package org.example.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.example.entity.Outbox;

@ApplicationScoped
public class OutboxRepository implements PanacheRepository<Outbox> {

}
