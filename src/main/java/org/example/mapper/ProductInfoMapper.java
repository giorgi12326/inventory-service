package org.example.mapper;

import org.example.dto.InventoryResponseDTO;
import org.example.dto.ProductInfoDTO;
import org.example.dto.UpdateQuantityFromInventory;
import org.example.entity.Inventory;
import org.example.entity.ProductInfo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface ProductInfoMapper {

    ProductInfoDTO toDTO(ProductInfo inventory);
    UpdateQuantityFromInventory toQuantityDTO(ProductInfo inventory);
    List<ProductInfoDTO> toDTOs(List<ProductInfo> inventory);

    ProductInfo toEntity(ProductInfoDTO inventory);


}
