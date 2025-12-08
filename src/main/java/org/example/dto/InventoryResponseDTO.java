package org.example.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class InventoryResponseDTO {
    private Long id;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ProductInfoDTO> products;
}
