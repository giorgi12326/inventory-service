package org.example.service;

import io.quarkus.arc.properties.IfBuildProperty;
import org.example.dto.Event;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.concurrent.CompletionStage;

@ApplicationScoped
@IfBuildProperty(name = "kafka.enabled", stringValue = "true")
public class ProductProducer {

    @Channel("products-out")
    Emitter<Event> productEmitter;

    public CompletionStage<Void> send(Event event) {
        return productEmitter.send(event);// sends the message to Kafka
    }
}