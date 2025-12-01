package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ProductInfoDTO {
    private Long id;
    private Long productId;
    private Integer quantity;
    private Long inventoryId;
}
