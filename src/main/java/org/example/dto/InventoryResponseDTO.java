package org.example.dto;

import org.example.entity.Location;

import java.time.LocalDateTime;
import java.util.List;

public class InventoryResponseDTO {
    private Location location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ProductInfoDTO> products;

}
