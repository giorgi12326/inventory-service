package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
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

    @Transactional
    public InventoryResponseDTO addInventory(InventoryRequestDTO inventoryRequestDTO) {
        Inventory inventory = inventoryMapper.toEntity(inventoryRequestDTO);
        inventoryRepository.persistAndFlush(inventory);
        System.out.println(inventory.getId());
        return inventoryMapper.toDTO(inventory);
    }
}