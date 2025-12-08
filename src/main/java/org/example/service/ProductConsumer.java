package org.example.service;

import io.quarkus.arc.properties.IfBuildProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.example.dto.Event;
import org.example.dto.ReserveProductDTO;
import org.example.entity.ProductInfo;
import org.example.repository.InventoryRepository;
import org.example.repository.ProductInfoRepository;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
@IfBuildProperty(name = "kafka.enabled", stringValue = "true")
public class ProductConsumer {

    @Inject
    ProductConsumer self;

    @Inject
    ProductInfoRepository productInfoRepository;

    @Incoming("products")
    public void consume(Event event) {
        if(event.getEventType().equals("PRODUCT_DELETED")){
            self.productDeleted(event);
        }
    }

    @Transactional
    public void productDeleted(Event event) {
        Long payload = (Long) event.getPayload();
        ProductInfo productInfo = productInfoRepository.findByProductId(payload).orElseThrow(() -> new RuntimeException("not found product!"));
        productInfo.setQuantity(0);
        productInfoRepository.persist(productInfo);

        System.out.println("cleared Quantity of  product with id: " +  productInfo.getProductId() + " from inventory");
    }
}