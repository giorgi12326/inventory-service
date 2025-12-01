package org.example.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import org.example.entity.Outbox;
import org.example.entity.ProductInfo;

public class OutboxRepository implements PanacheRepository<Outbox> {

}
