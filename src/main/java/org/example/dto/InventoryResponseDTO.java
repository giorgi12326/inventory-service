package org.example.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entity.Location;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class InventoryResponseDTO {
    private Long id;
    private Location location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ProductInfoDTO> products;

}
