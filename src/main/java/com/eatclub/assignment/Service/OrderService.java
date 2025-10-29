package com.eatclub.assignment.Service;


import com.eatclub.assignment.DTO.OrderRequest;
import com.eatclub.assignment.DTO.OrderResponse;
import com.eatclub.assignment.Entity.OrderEntity;
import com.eatclub.assignment.Entity.OrderItemEntity;
import com.eatclub.assignment.Entity.OrderStatus;
import com.eatclub.assignment.Repository.OrderItemRepository;
import com.eatclub.assignment.Repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final RedisOrderEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository, RedisOrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // Create order
        OrderEntity order = new OrderEntity();
        order.setCustomerId(request.customerId);
        order.setStatus(OrderStatus.PLACED);

        // Map order items
        List<OrderItemEntity> items = request.items.stream()
                .map(i -> OrderItemEntity.builder()
                        .order(order)
                        .productId(i.productId)
                        .quantity(i.quantity)
                        .price(i.price)
                        .build()
                )
                .collect(Collectors.toList());

        order.setItems(items);

        // Calculate total
        BigDecimal total = items.stream()
                .map(it -> it.getPrice().multiply(BigDecimal.valueOf(it.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);

        // Persist order (cascade saves items too)
        OrderEntity savedOrder = orderRepository.saveAndFlush(order);

        // Emit event to Redis Stream
        eventPublisher.publishOrderEvent(savedOrder.getId(), savedOrder.getStatus(), savedOrder.getCustomerId());

        return toResponse(savedOrder);
    }

    @Transactional
    public OrderResponse updateOrderStatus(UUID orderId, OrderStatus status) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        order.setStatus(status);
        OrderEntity updatedOrder = orderRepository.saveAndFlush(order);

        // Publish status update event
        eventPublisher.publishOrderEvent(updatedOrder.getId(), updatedOrder.getStatus(), updatedOrder.getCustomerId());

        return toResponse(updatedOrder);
    }

    public OrderResponse getOrder(UUID orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        return toResponse(order);
    }

    // Helper mapper method
    private OrderResponse toResponse(OrderEntity e) {
        OrderResponse r = new OrderResponse();
        r.id = e.getId();
        r.customerId = e.getCustomerId();
        r.status = e.getStatus();
        r.totalAmount = e.getTotalAmount();
        r.createdAt = e.getCreatedAt();
        r.updatedAt = e.getUpdatedAt();

        r.items = e.getItems().stream()
                .map(it -> {
                    OrderResponse.OrderItem oi = new OrderResponse.OrderItem();
                    oi.productId = it.getProductId();
                    oi.quantity = it.getQuantity();
                    oi.price = it.getPrice();
                    return oi;
                })
                .collect(Collectors.toList());

        return r;
    }
}