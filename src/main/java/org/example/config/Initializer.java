package org.example.config;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import org.example.dto.InventoryRequestDTO;
import org.example.dto.ProductInfoDTO;
import org.example.service.InventoryService;
import org.example.service.ProductService;

@Singleton
public class Initializer {
    @Inject
    ProductService productService;

    @Inject
    InventoryService inventoryService;

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        InventoryRequestDTO inventoryRequestDTO = InventoryRequestDTO.builder()
                .location("Giorgi's Inventory")
                .build();
        inventoryService.addInventory(inventoryRequestDTO);

        ProductInfoDTO productInfo = ProductInfoDTO.builder()
                .productId(1L)
                .quantity(120)
                .inventoryId(1L)
                .build();
        productService.addProduct(productInfo);

        ProductInfoDTO productInfo1 = ProductInfoDTO.builder()
                .productId(2L)
                .quantity(40)
                .inventoryId(1L)
                .build();
        productService.addProduct(productInfo1);

        ProductInfoDTO productInfo2 = ProductInfoDTO.builder()
                .productId(3L)
                .quantity(30)
                .inventoryId(1L)
                .build();
        productService.addProduct(productInfo2);
    }
}