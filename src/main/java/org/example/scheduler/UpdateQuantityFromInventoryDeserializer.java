package org.example.scheduler;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;
import org.example.dto.UpdateQuantityFromInventory;

public class UpdateQuantityFromInventoryDeserializer extends JsonbDeserializer<UpdateQuantityFromInventory> {
    public UpdateQuantityFromInventoryDeserializer() {
        super(UpdateQuantityFromInventory.class);
    }
}