package com.bellwin.stock.config;

import com.bellwin.stock.command.OrderRequestEvent;
import com.bellwin.stock.execution.OrderExecutedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaConfig {

    public static final String ORDER_REQUESTS_TOPIC = "order-requests";
    public static final String ORDER_EXECUTED_TOPIC = "order-executed";

    @Bean
    public NewTopic orderRequestsTopic() {
        return TopicBuilder.name(ORDER_REQUESTS_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic orderExecutedTopic() {
        return TopicBuilder.name(ORDER_EXECUTED_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public JsonSerializer<OrderRequestEvent> orderRequestJsonSerializer() {
        return new JsonSerializer<>();
    }

    @Bean
    public JsonDeserializer<OrderRequestEvent> orderRequestJsonDeserializer() {
        JsonDeserializer<OrderRequestEvent> deserializer = new JsonDeserializer<>(OrderRequestEvent.class);
        deserializer.addTrustedPackages("com.bellwin.stock.*");
        deserializer.setUseTypeHeaders(false);
        return deserializer;
    }

    @Bean
    public JsonSerializer<OrderExecutedEvent> orderExecutedJsonSerializer() {
        return new JsonSerializer<>();
    }
}
