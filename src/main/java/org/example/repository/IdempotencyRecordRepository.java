package org.example.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.example.entity.IdempotencyRecord;


@ApplicationScoped
public class IdempotencyRecordRepository implements PanacheRepositoryBase<IdempotencyRecord,String> {
}
