package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.example.dto.*;
import org.example.entity.Inventory;
import org.example.entity.ProductInfo;
import org.example.mapper.ProductInfoMapper;
import org.example.repository.InventoryRepository;
import org.example.repository.ProductInfoRepository;

import java.time.Instant;
import java.util.ArrayList;
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
        ProductInfo productEntity = productInfoRepository.findByProductId(productInfoDTO.getProductId()).orElseThrow(() -> new RuntimeException("product Not Found with ID: " + productInfoDTO.getProductId()));

        productEntity.setQuantity(productInfoDTO.getQuantity());
        productInfoRepository.persist(productEntity);

        ProductInfoDTO quantityDTO = productInfoMapper.toDTO(productEntity);
        Event event = new Event(EventType.UPDATED, Instant.now(), productInfoDTO);
        productProducer.send(event);
        return quantityDTO;
    }

    @Transactional
    public List<ReserveProductDTO> getAndReserveProducts(List<ReserveProductDTO> reserveProductDTO) {
        List<ReserveProductDTO> reserveList = new ArrayList<>();
        for(ReserveProductDTO productDTO : reserveProductDTO) {
            ProductInfo product = productInfoRepository.findByProductId(productDTO.getProductId()).orElseThrow(() -> new RuntimeException("product Not Found with ID: " + productDTO.getProductId()));
            Integer productQuantity = product.getQuantity();
            Integer dtoQuantity = productDTO.getQuantity();

            if(productQuantity - dtoQuantity >= 0)
                product.setQuantity(productQuantity - dtoQuantity);
            else
                throw new IllegalStateException("Not enough stock for product " + product.getId());

            productInfoRepository.persist(product);
            reserveList.add(ReserveProductDTO.builder().productId(productDTO.getProductId()).quantity(dtoQuantity).build());
        }

        return reserveList;
    }

    @Transactional
    public void releaseProducts(List<ReserveProductDTO> reserveProductDTO) {
        List<ProductInfo> productList = new ArrayList<>();
        for (ReserveProductDTO dto : reserveProductDTO) {
            ProductInfo product = productInfoRepository.findByProductId(dto.getProductId()).orElseThrow(() -> new RuntimeException("product Not Found with ID: " + dto.getProductId()));
            product.setQuantity(product.getQuantity() + dto.getQuantity());
            productList.add(product);
        }
        productInfoRepository.persist(productList);
    }


}
