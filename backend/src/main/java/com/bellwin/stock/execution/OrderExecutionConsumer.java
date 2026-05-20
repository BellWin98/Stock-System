package com.bellwin.stock.execution;

import com.bellwin.stock.command.OrderRequestEvent;
import com.bellwin.stock.config.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderExecutionConsumer {

    private final ExecutionService executionService;

    @KafkaListener(topics = KafkaConfig.ORDER_REQUESTS_TOPIC, groupId = "execution-engine")
    public void onOrderRequest(OrderRequestEvent event) {
        log.debug("Received order request: {}", event.orderId());
        executionService.processOrderRequest(event);
    }
}
