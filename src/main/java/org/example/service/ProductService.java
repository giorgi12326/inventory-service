package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.example.dto.ProductInfoDTO;
import org.example.entity.Inventory;
import org.example.entity.ProductInfo;
import org.example.mapper.ProductInfoMapper;
import org.example.repository.InventoryRepository;
import org.example.repository.ProductInfoRepository;

import java.util.List;

@ApplicationScoped()
public class ProductService {

    @Inject
    ProductInfoRepository productInfoRepository;

    @Inject
    ProductInfoMapper productInfoMapper;
    @Inject
    InventoryRepository inventoryRepository;


    public List<ProductInfoDTO> getProducts() {
        return productInfoMapper.toDTOs(productInfoRepository.findAll().list());
    }

    @Transactional
    public ProductInfoDTO addProduct(ProductInfoDTO productInfoDTO) {
        ProductInfo productEntity = productInfoMapper.toEntity(productInfoDTO);

        Inventory inventory = inventoryRepository.findById(productInfoDTO.getInventoryId());
        productEntity.setInventory(inventory);

        inventory.getProducts().add(productEntity);

        productInfoRepository.persist(productEntity);
        return productInfoMapper.toDTO(productEntity);
    }

}
