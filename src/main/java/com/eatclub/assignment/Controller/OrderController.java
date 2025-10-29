package com.eatclub.assignment.Controller;

import com.eatclub.assignment.DTO.OrderRequest;
import com.eatclub.assignment.DTO.OrderResponse;
import com.eatclub.assignment.DTO.UpdateStatusRequest;
import com.eatclub.assignment.Service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse r = orderService.createOrder(request);
        return ResponseEntity.ok(r);
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateStatusRequest req) {
        OrderResponse r = orderService.updateOrderStatus(orderId, req.status);
        return ResponseEntity.ok(r);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId) {
        OrderResponse r = orderService.getOrder(orderId);
        return ResponseEntity.ok(r);
    }
}