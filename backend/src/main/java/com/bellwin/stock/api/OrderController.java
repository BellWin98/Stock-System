package com.bellwin.stock.api;

import com.bellwin.stock.api.dto.CreateOrderRequest;
import com.bellwin.stock.api.dto.OrderResponse;
import com.bellwin.stock.command.OrderCommandService;
import com.bellwin.stock.infra.RedisRateLimiter;
import com.bellwin.stock.query.OrderQueryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderCommandService orderCommandService;
    private final OrderQueryService orderQueryService;
    private final RedisRateLimiter rateLimiter;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader("X-Account-Id") Long accountId,
            @Valid @RequestBody CreateOrderRequest request,
            HttpServletRequest httpRequest) {
        String rateLimitKey = accountId + ":" + resolveClientKey(httpRequest);
        if (!rateLimiter.tryAcquire(rateLimitKey)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
        }
        OrderResponse response = orderCommandService.submitOrder(accountId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping
    public List<OrderResponse> getOrders(@RequestParam Long accountId) {
        return orderQueryService.getOrders(accountId);
    }

    private String resolveClientKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
