package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.example.dto.Event;
import org.example.dto.ReserveForOrderDTO;
import org.example.dto.ReserveProductDTO;
import org.example.entity.Outbox;
import org.example.entity.OutboxStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class OrderConsumer {
    @Inject
    OrderConsumer self;

    @Inject
    ProductService productService;

    @Incoming("orders")
    public void consume(Event event) {
        if(event.getEventType().equals("RESERVE_PRODUCTS")){
            self.reserveProducts(event);
        }
    }
    public void reserveProducts(Event event) {
        ReserveForOrderDTO reserveForOrder = (ReserveForOrderDTO) event.getPayload();
        try {
            productService.reserveProducts(reserveForOrder, "inventory:" + UUID.randomUUID());
        }
        catch (Exception e) {
            productService.sendFailForReserveForOrder(reserveForOrder);
      }
    }

}
