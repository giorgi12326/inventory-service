package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.dto.InventoryRequestDTO;
import org.example.dto.InventoryResponseDTO;
import org.example.entity.Inventory;
import org.example.mapper.InventoryMapper;
import org.example.repository.InventoryRepository;

import java.util.List;

@ApplicationScoped
public class InventoryService {

    @Inject
    InventoryRepository inventoryRepository;

    @Inject
    InventoryMapper inventoryMapper;

    public List<InventoryResponseDTO> getInventories() {
        return inventoryMapper.toDTOs(inventoryRepository.findAll().list());
    }

    public InventoryResponseDTO addInventory(InventoryRequestDTO inventoryRequestDTO) {
        Inventory inventory = inventoryMapper.toEntity(inventoryRequestDTO);
        inventoryRepository.persist(inventory);
        return inventoryMapper.toDTO(inventory);
    }
}