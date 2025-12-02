package org.example.outbox;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.dto.Event;
import org.example.dto.ReserveProductDTO;
import org.example.service.ProductProducer;
import org.example.service.ProductService;

import java.util.List;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class ProductReleaseHandler  implements EventHandler{

    @Inject
    ProductService productService;
    @Inject
    ProductProducer productProducer;

    @Override
    public CompletionStage<Void> process(Event payload) {
        return productProducer.send(payload);

    }

    @Override
    public void compensate(Event payload) {
        productService.getAndReserveProducts((List<ReserveProductDTO>) payload);
    }
}
