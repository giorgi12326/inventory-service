package org.example.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import org.example.dto.*;
import org.example.entity.*;
import org.example.mapper.ProductInfoMapper;
import org.example.repository.IdempotencyRecordRepository;
import org.example.repository.InventoryRepository;
import org.example.repository.OutboxRepository;
import org.example.repository.ProductInfoRepository;
import org.hibernate.exception.ConstraintViolationException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@ApplicationScoped()
public class ProductService {
    @Inject
    Jsonb jsonb;

    @Inject
    ProductInfoRepository productInfoRepository;

    @Inject
    ProductInfoMapper productInfoMapper;

    @Inject
    InventoryRepository inventoryRepository;

    @Inject
    OutboxRepository outboxRepository;

    @Inject
    IdempotencyRecordRepository idempotentRecordRepository;

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

        ProductInfoDTO dto = productInfoMapper.toDTO(productEntity);

        Event event = new Event(EventType.UPDATED, Instant.now(), productInfoDTO);
        outboxRepository.persist(Outbox.builder().eventType("PRODUCT_CREATE").event(jsonb.toJson(event)).status(OutboxStatus.PENDING).build());

        return dto;
    }

    @Transactional
    public ProductInfoDTO updateProduct(UpdateQuantityFromInventory productInfoDTO) {
        ProductInfo productEntity = productInfoRepository.findByProductId(productInfoDTO.getProductId()).orElseThrow(() -> new RuntimeException("product Not Found with ID: " + productInfoDTO.getProductId()));

        productEntity.setQuantity(productInfoDTO.getQuantity());
        productInfoRepository.persist(productEntity);

        ProductInfoDTO quantityDTO = productInfoMapper.toDTO(productEntity);

        Event event = new Event(EventType.UPDATED, Instant.now(), productInfoDTO);
        outboxRepository.persist(Outbox.builder().eventType("PRODUCT_UPDATE").event(jsonb.toJson(event)).status(OutboxStatus.PENDING).build());
        return quantityDTO;
    }

    @Transactional
    public List<ReserveProductDTO> getAndReserveProducts(List<ReserveProductDTO> reserveProductDTO, String idempotencyKey) {
        if(idempotencyKey==null){
            throw new BadRequestException("missing the idempotency-key!");
        }

        IdempotencyRecord byId = idempotentRecordRepository.findById(idempotencyKey);
        if(byId != null){
            return Arrays.asList(jsonb.fromJson(byId.getResponseJson(), ReserveProductDTO[].class));
        }

        IdempotencyRecord record = IdempotencyRecord.builder()
                .idempotencyKey(idempotencyKey)
                .actionType("PRODUCTS_RESERVE")
                .requestJson(jsonb.toJson(reserveProductDTO))
                .build();

        List<ReserveProductDTO> reserveList = new ArrayList<>();
        List<ReserveProductDTO> eventList = new ArrayList<>();
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
            eventList.add(ReserveProductDTO.builder().productId(productDTO.getProductId()).quantity(productQuantity-dtoQuantity).build());
        }

        eventList.forEach((productInfoDTO)->{
            Event event = new Event(EventType.UPDATED, Instant.now(), productInfoDTO);
            outboxRepository.persist(Outbox.builder().eventType("PRODUCTS_RESERVE").event(jsonb.toJson(event)).status(OutboxStatus.PENDING).build());
        });

        record.setResponseJson(jsonb.toJson(reserveList));
        record.persist();

        return reserveList;
    }

    @Transactional
    public void releaseProducts(String idempotencyKey) {
        IdempotencyRecord existingOne = idempotentRecordRepository.findById(idempotencyKey);
        if(existingOne != null){
            return;
        }

        IdempotencyRecord byId = idempotentRecordRepository.findById(idempotencyKey.substring(11));
        if(byId == null) throw new RuntimeException("cant compensate non existing action");

        ReserveProductDTO[] reserveProductDTO = jsonb.fromJson(byId.getRequestJson(), ReserveProductDTO[].class);

        List<ProductInfo> productList = new ArrayList<>();
        for (ReserveProductDTO dto : reserveProductDTO) {
            ProductInfo product = productInfoRepository.findByProductId(dto.getProductId()).orElseThrow(() -> new RuntimeException("product Not Found with ID: " + dto.getProductId()));
            product.setQuantity(product.getQuantity() + dto.getQuantity());
            productList.add(product);
        }
        productInfoRepository.persist(productList);

        List<ReserveProductDTO> list = productList.stream().map(dto -> ReserveProductDTO.builder().productId(dto.getProductId()).quantity(dto.getQuantity()).build()).toList();
        list.forEach((productInfoDTO)->{
            Event event = new Event(EventType.UPDATED, Instant.now(), productInfoDTO);
            outboxRepository.persist(Outbox.builder().eventType("PRODUCTS_RELEASE").event(jsonb.toJson(event)).status(OutboxStatus.PENDING).build());
        });

        IdempotencyRecord record = IdempotencyRecord.builder()
                .idempotencyKey(idempotencyKey)
                .actionType("COMPENSATION_PRODUCTS_RESERVE")
                .build();
        record.persist();
    }
}
