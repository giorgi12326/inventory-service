package org.example.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import org.example.dto.*;
import org.example.entity.*;
import org.example.mapper.ProductInfoMapper;
import org.example.repository.IdempotencyRecordRepository;
import org.example.repository.InventoryRepository;
import org.example.repository.OutboxRepository;
import org.example.repository.ProductInfoRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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

        Event event = new Event("UPDATED", Instant.now(), productInfoDTO);
        outboxRepository.persist(Outbox.builder().destination("product-topic").destination("order").eventType("PRODUCT_CREATE").event(jsonb.toJson(event)).status(OutboxStatus.PENDING).build());

        return dto;
    }

    @Transactional
    public ProductInfoDTO updateProduct(UpdateQuantityFromInventory productInfoDTO) {
        ProductInfo productEntity = productInfoRepository.findByProductId(productInfoDTO.getProductId()).orElseThrow(() -> new RuntimeException("product Not Found with ID: " + productInfoDTO.getProductId()));

        productEntity.setQuantity(productInfoDTO.getQuantity());
        productInfoRepository.persist(productEntity);

        ProductInfoDTO quantityDTO = productInfoMapper.toDTO(productEntity);

        Event event = new Event("UPDATED", Instant.now(), productInfoDTO);
        outboxRepository.persist(Outbox.builder().destination("product-topic").eventType("PRODUCT_UPDATE").event(jsonb.toJson(event)).status(OutboxStatus.PENDING).build());
        return quantityDTO;
    }

    @Transactional
    public void reserveProducts(ReserveForOrderDTO reserveForOrder, String idempotencyKey) {
        if(idempotencyKey==null){
            throw new BadRequestException("missing the idempotency-key!");
        }

        IdempotencyRecord byId = idempotentRecordRepository.findById(idempotencyKey);
        if(byId != null){
            return;
        }

        IdempotencyRecord record = IdempotencyRecord.builder()
                .idempotencyKey(idempotencyKey)
                .actionType("PRODUCTS_RESERVE")
                .requestJson(jsonb.toJson(reserveForOrder))
                .build();

        List<ReserveProductDTO> eventList = new ArrayList<>();
        for(ReserveProductDTO productDTO : reserveForOrder.getReserveProducts()) {
            ProductInfo product = productInfoRepository.findByProductId(productDTO.getProductId()).orElseThrow(() -> new RuntimeException("product Not Found with ID: " + productDTO.getProductId()));
            Integer productQuantity = product.getQuantity();
            Integer dtoQuantity = productDTO.getQuantity();

            if(productQuantity - dtoQuantity >= 0)
                product.setQuantity(productQuantity - dtoQuantity);
            else
                throw new IllegalStateException("Not enough stock for product " + product.getId());

            productInfoRepository.persist(product);
            eventList.add(ReserveProductDTO.builder().productId(productDTO.getProductId()).quantity(productQuantity-dtoQuantity).build());
        }

        eventList.forEach((productInfoDTO)->{
            Event event = new Event("UPDATED", Instant.now(), productInfoDTO);
            outboxRepository.persist(Outbox.builder().destination("product-topic").eventType("PRODUCTS_RESERVED").event(jsonb.toJson(event)).status(OutboxStatus.PENDING).build());
        });
        Event event = new Event("PRODUCTS_RESERVED", Instant.now(), reserveForOrder.getOrderId());
        outboxRepository.persist(Outbox.builder().destination("order-topic").eventType("PRODUCTS_RESERVED").event(jsonb.toJson(event)).status(OutboxStatus.PENDING).build());

        record.persist();

    }

    @Transactional
    public void compensateReserveProducts(String idempotencyKey) {
        IdempotencyRecord previous = idempotentRecordRepository.findById(idempotencyKey);
        if(previous != null) {
            return;
        }

        IdempotencyRecord byId = idempotentRecordRepository.findById(idempotencyKey.substring(11));
        if(byId == null) throw new RuntimeException("cant compensate non existing action");

        ReserveProductDTO[] reserveProductDTO = jsonb.fromJson(byId.getRequestJson(), ReserveProductDTO[].class);

        releaseTheseProductsLogic(Arrays.stream(reserveProductDTO).toList());

        IdempotencyRecord record = IdempotencyRecord.builder()
                .idempotencyKey(idempotencyKey)
                .actionType("COMPENSATION_PRODUCTS_RESERVE")
                .build();
        record.persist();
    }

    public void releaseTheseProducts(List<ReserveProductDTO> reserveProductDTO,  String idempotencyKey) {
        if(idempotencyKey==null){
            throw new BadRequestException("missing the idempotency-key!");
        }

        IdempotencyRecord byId = idempotentRecordRepository.findById(idempotencyKey);
        if(byId != null){
            return;
        }
        releaseTheseProductsLogic(reserveProductDTO);

        IdempotencyRecord record = IdempotencyRecord.builder()
                .idempotencyKey(idempotencyKey)
                .actionType("PRODUCTS_RESERVE")
                .requestJson(jsonb.toJson(reserveProductDTO))
                .build();
        idempotentRecordRepository.persist(record);

    }

    public void releaseTheseProductsLogic(List<ReserveProductDTO> reserveProductDTO) {
        List<ProductInfo> productList = new ArrayList<>();
        for (ReserveProductDTO dto : reserveProductDTO) {
            ProductInfo product = productInfoRepository.findByProductId(dto.getProductId()).orElseThrow(() -> new RuntimeException("product Not Found with ID: " + dto.getProductId()));
            product.setQuantity(product.getQuantity() + dto.getQuantity());
            productList.add(product);
        }
        productInfoRepository.persist(productList);

        List<ReserveProductDTO> list = productList.stream().map(dto -> ReserveProductDTO.builder().productId(dto.getProductId()).quantity(dto.getQuantity()).build()).toList();
        list.forEach((productInfoDTO)->{
            Event event = new Event("UPDATED", Instant.now(), productInfoDTO);
            outboxRepository.persist(Outbox.builder().destination("product-topic").eventType("PRODUCTS_RELEASE").event(jsonb.toJson(event)).status(OutboxStatus.PENDING).build());
        });
    }

    public void sendFailForReserveForOrder(ReserveForOrderDTO reserveForOrder) {
        Event event = new Event("PRODUCTS_RESERVE_FAILED", Instant.now(), reserveForOrder.getOrderId());
        outboxRepository.persist(Outbox.builder().destination("order-topic").eventType("PRODUCTS_RESERVED").event(jsonb.toJson(event)).status(OutboxStatus.PENDING).build());
    }
}
