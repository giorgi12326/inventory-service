package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.example.dto.Event;
import org.example.dto.EventType;
import org.example.dto.ProductInfoDTO;
import org.example.dto.UpdateQuantityFromInventory;
import org.example.entity.Inventory;
import org.example.entity.ProductInfo;
import org.example.mapper.ProductInfoMapper;
import org.example.repository.InventoryRepository;
import org.example.repository.ProductInfoRepository;

import java.time.Instant;
import java.util.List;

@ApplicationScoped()
public class ProductService {

    @Inject
    ProductInfoRepository productInfoRepository;

    @Inject
    ProductInfoMapper productInfoMapper;

    @Inject
    InventoryRepository inventoryRepository;

    @Inject
    ProductProducer productProducer;


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

    @Transactional
    public ProductInfoDTO updateProduct(UpdateQuantityFromInventory productInfoDTO) {
        ProductInfo productEntity = productInfoRepository.findByProductId(productInfoDTO.getProductId());

        productEntity.setQuantity(productInfoDTO.getQuantity());
        productInfoRepository.persist(productEntity);

        ProductInfoDTO quantityDTO = productInfoMapper.toDTO(productEntity);
        Event event = new Event(EventType.UPDATED, Instant.now(), productInfoDTO);
        productProducer.send(event);
        return quantityDTO;
    }

}
