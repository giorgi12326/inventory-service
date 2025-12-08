package org.example.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReserveForOrderDTO {
    Long orderId;
    List<ReserveProductDTO> reserveProducts;
}
