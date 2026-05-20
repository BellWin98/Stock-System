package com.bellwin.stock.command;

import com.bellwin.stock.config.KafkaConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final KafkaTemplate<String, OrderRequestEvent> kafkaTemplate;

    public void publishOrderRequest(OrderRequestEvent event) {
        kafkaTemplate.send(KafkaConfig.ORDER_REQUESTS_TOPIC, event.orderId(), event);
    }
}
