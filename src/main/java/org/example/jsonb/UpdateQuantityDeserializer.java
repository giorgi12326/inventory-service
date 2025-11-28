package org.example.jsonb;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;
import org.example.dto.UpdateQuantityFromInventory;

public class UpdateQuantityDeserializer extends JsonbDeserializer<UpdateQuantityFromInventory> {
    public UpdateQuantityDeserializer() {
        super(UpdateQuantityFromInventory.class);
    }
}