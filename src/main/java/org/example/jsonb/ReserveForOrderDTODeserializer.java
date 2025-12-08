package org.example.jsonb;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;
import org.example.dto.ReserveForOrderDTO;

public class ReserveForOrderDTODeserializer extends JsonbDeserializer<ReserveForOrderDTO> {
    public ReserveForOrderDTODeserializer() {
        super(ReserveForOrderDTO.class);
    }
}