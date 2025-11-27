package org.example.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.example.entity.Inventory;


@ApplicationScoped
public class InventoryRepository implements PanacheRepository<Inventory> {

}
