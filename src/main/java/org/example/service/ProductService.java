package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.example.dto.ProductInfoDTO;
import org.example.entity.ProductInfo;
import org.example.mapper.ProductInfoMapper;
import org.example.repository.ProductInfoRepository;

import java.util.List;

@ApplicationScoped()
public class ProductService {

    @Inject
    ProductInfoRepository productInfoRepository;

    @Inject
    ProductInfoMapper productInfoMapper;

    public ProductInfoDTO addProduct(ProductInfoDTO productInfo) {
        ProductInfo productEntity = productInfoMapper.toEntity(productInfo);
        productInfoRepository.persist(productEntity);
        return productInfoMapper.toDTO(productEntity);
    }

}
