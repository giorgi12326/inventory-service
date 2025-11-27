package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ApplicationPath;
import org.example.dto.InventoryResponseDTO;
import org.example.mapper.InventoryMapper;
import org.example.repository.InventoryRepository;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped()
public class InventoryService {

    @Inject
    InventoryRepository inventoryRepository;

    @Inject
    InventoryMapper inventoryMapper;

    public List<InventoryResponseDTO> getInventories() {
        return inventoryRepository.findAll()
                .stream()
                .map(inventoryMapper::toDTO)
                .toList();

    }

}
