package org.example.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entity.Location;

@Data
@NoArgsConstructor
public class InventoryRequestDTO {
    private Location location;
}
