package org.example.scheduler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import io.quarkus.kafka.client.serialization.JsonbSerializer;
import org.example.dto.Event;

import java.util.Properties;

@ApplicationScoped
public class KafkaProducerFactory {

    @Produces
    @ApplicationScoped
    public KafkaProducer<String, Event> producer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-0.kafka:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonbSerializer.class.getName());

        return new KafkaProducer<>(props);
    }
}
