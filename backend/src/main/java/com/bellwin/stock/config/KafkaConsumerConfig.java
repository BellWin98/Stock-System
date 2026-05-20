package com.bellwin.stock.config;

import com.bellwin.stock.command.OrderRequestEvent;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, OrderRequestEvent> orderRequestConsumerFactory(
            org.springframework.boot.autoconfigure.kafka.KafkaProperties kafkaProperties) {
        Map<String, Object> config = new HashMap<>(kafkaProperties.buildConsumerProperties());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.bellwin.stock.*");
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, OrderRequestEvent.class.getName());
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
                new JsonDeserializer<>(OrderRequestEvent.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderRequestEvent> kafkaListenerContainerFactory(
            ConsumerFactory<String, OrderRequestEvent> orderRequestConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, OrderRequestEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderRequestConsumerFactory);
        factory.setConcurrency(1);
        return factory;
    }
}
