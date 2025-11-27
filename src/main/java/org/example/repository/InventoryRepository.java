package org.example.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.example.entity.Inventory;

import java.util.List;

@ApplicationScoped
public class InventoryRepository implements PanacheRepository<Inventory> {

}
