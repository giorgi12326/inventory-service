package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.example.dto.Event;
import org.example.dto.ReserveForOrderDTO;
import java.util.UUID;

@ApplicationScoped
public class OrderConsumer {
    @Inject
    ProductService productService;

    @Incoming("orders")
    public void consume(Event event) {
        System.out.println("RECIEVED EVENT OF TYPE" + event.getEventType());
        if(event.getEventType().equals("RESERVE_PRODUCTS")){
            reserveProducts(event);
        }
    }

    private void reserveProducts(Event event) {
        ReserveForOrderDTO reserveForOrder = (ReserveForOrderDTO) event.getPayload();
        try {
            productService.reserveProducts(reserveForOrder, "inventory-" + UUID.randomUUID());
        }
        catch (Exception e) {
            productService.sendFailForReserveForOrder(reserveForOrder);
      }
    }

}
