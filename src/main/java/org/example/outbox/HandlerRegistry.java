package org.example.outbox;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.Map;

@ApplicationScoped
public class HandlerRegistry {

    private final Map<String, EventHandler> handlers = new HashMap<>();

    @Inject
    ProductReservedHandler productReservedHandler;

    @Inject
    ProductUpdateHandler productUpdateHandler;

    @Inject
    ProductReleaseHandler productReleaseHandler;


    @PostConstruct
    void init() {
        handlers.put("PRODUCTS_RESERVED", productReservedHandler);
        handlers.put("PRODUCT_UPDATE", productUpdateHandler);
        handlers.put("PRODUCTS_RELEASE", productReleaseHandler);
        handlers.put("PRODUCT_CREATE", productReleaseHandler);
    }

    public EventHandler getHandler(String eventType) {
        return handlers.get(eventType);
    }
}
