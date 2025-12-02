package org.example.outbox;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.dto.Event;
import org.example.service.ProductProducer;

import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class ProductCreateHandler implements EventHandler {

    @Inject
    ProductProducer productProducer;

    @Override
    public CompletionStage<Void> process(Event payload) {
        return productProducer.send(payload);

    }

    @Override
    public void compensate(Event payload) {
    }
}
