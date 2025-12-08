package org.example.jsonb;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;
import org.example.dto.ReserveForOrderDTO;
import org.example.dto.UpdateQuantityFromInventory;

public class ReserveForOrderDTODeserializer extends JsonbDeserializer<UpdateQuantityFromInventory> {
    public ReserveForOrderDTODeserializer() {
        super(ReserveForOrderDTO.class);
    }
}