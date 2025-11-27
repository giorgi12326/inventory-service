package org.example.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductInfoDTO {
    private Long id;
    private Long productId;
    private Long quantity;
    private Long inventoryId;
}
