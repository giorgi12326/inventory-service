package org.example.mapper;

import org.example.dto.InventoryRequestDTO;
import org.example.dto.InventoryResponseDTO;
import org.example.entity.Inventory;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface InventoryMapper {
    InventoryResponseDTO toDTO(Inventory inventory);
    List<InventoryResponseDTO> toDTOs(List<Inventory> inventory);

    Inventory toEntity(InventoryRequestDTO inventory);
    List<Inventory> toEntities(List<InventoryRequestDTO> inventory);

}
