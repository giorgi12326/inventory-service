package org.example.service;

import org.example.dto.UpdateQuantityFromInventory;
import org.example.entity.ProductInfo;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class ProductProducer {

    @Channel("products-out")          // channel name matches the property
    Emitter<UpdateQuantityFromInventory> productEmitter;

    public void send(UpdateQuantityFromInventory updateQuantityFromInventory) {
        productEmitter.send(updateQuantityFromInventory);   // sends the message to Kafka
    }
}