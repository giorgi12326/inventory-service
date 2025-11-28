package org.example.service;

import org.example.dto.Event;
import org.example.dto.UpdateQuantityFromInventory;
import org.example.entity.ProductInfo;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class ProductProducer {

    @Channel("products-out")
    Emitter<Event> productEmitter;

    public void send(Event event) {
        productEmitter.send(event);   // sends the message to Kafka
    }
}