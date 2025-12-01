package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.example.dto.Event;
import org.example.repository.InventoryRepository;

@ApplicationScoped
public class ProductConsumer {

    @Inject
    InventoryRepository inventoryRepository;

    @Incoming("products")
    public void consume(Event event) {
        Long payload = (Long) event.getPayload();
        long l = inventoryRepository.deleteByProductId(payload);

        System.out.println("Deleted product with id: " +  l + " from inventory");
    }
}