package org.example.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.example.entity.Outbox;

public class OutboxRepository implements PanacheRepository<Outbox> {

}
