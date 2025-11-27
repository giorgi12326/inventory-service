package org.example.mapper;

import org.example.dto.InventoryResponseDTO;
import org.example.dto.ProductInfoDTO;
import org.example.entity.Inventory;
import org.example.entity.ProductInfo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface InventoryMapper {
    InventoryResponseDTO toDTO(Inventory inventory);
    List<InventoryResponseDTO> toDTOs(List<Inventory> inventory);


    ProductInfoDTO toProductDTO(ProductInfo inventory);
    List<ProductInfoDTO> toProductDTOs(List<ProductInfo> inventory);


}
