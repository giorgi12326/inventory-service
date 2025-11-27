package org.example.mapper;

import org.example.dto.InventoryResponseDTO;
import org.example.entity.Inventory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface InventoryMapper {
    InventoryResponseDTO toDTO(Inventory inventory);
    Inventory toEntity(Inventory inventory);
}
