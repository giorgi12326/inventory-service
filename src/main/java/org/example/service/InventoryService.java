package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.dto.InventoryResponseDTO;
import org.example.dto.ProductInfoDTO;
import org.example.entity.ProductInfo;
import org.example.mapper.InventoryMapper;
import org.example.repository.InventoryRepository;

import java.util.List;

@ApplicationScoped()
public class InventoryService {

    @Inject
    InventoryRepository inventoryRepository;

    @Inject
    InventoryMapper inventoryMapper;

    public List<InventoryResponseDTO> getInventories() {
        return inventoryMapper.toDTOs(inventoryRepository.findAll().list());
    }


    public List<ProductInfoDTO> addProduct(ProductInfo productInfo) {
        return null;
    }
}
