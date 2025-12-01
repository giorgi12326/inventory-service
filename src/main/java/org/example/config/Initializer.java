package org.example.config;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.example.dto.Event;
import org.example.dto.EventType;
import org.example.dto.ProductInfoDTO;
import org.example.entity.ProductInfo;
import org.example.repository.ProductInfoRepository;
import org.example.service.ProductService;

import java.time.Instant;

@Singleton
public class Initializer {
    @Inject
    ProductService productService;

    @Transactional
    void onStart(@Observes StartupEvent ev) {

        ProductInfoDTO productInfo = ProductInfoDTO.builder()
                .productId(1L)
                .quantity(120)
                .build();
        productService.addProduct(productInfo);

        ProductInfoDTO productInfo1 = ProductInfoDTO.builder()
                .productId(2L)
                .quantity(40)
                .build();
        productService.addProduct(productInfo1);

        ProductInfoDTO productInfo2 = ProductInfoDTO.builder()
                .productId(3L)
                .quantity(30)
                .build();
        productService.addProduct(productInfo2);

    }
}