package org.example.dto;

import lombok.Data;

@Data
public class UpdateQuantityFromInventory {
    Long productId;
    Integer quantity;
}
