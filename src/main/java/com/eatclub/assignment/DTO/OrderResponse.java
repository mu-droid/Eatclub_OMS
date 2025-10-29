package com.eatclub.assignment.DTO;


import com.eatclub.assignment.Entity.OrderStatus;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public class OrderResponse {
    public UUID id;
    public String customerId;
    public OrderStatus status;
    public BigDecimal totalAmount;
    public List<OrderItem> items;
    public OffsetDateTime createdAt;
    public OffsetDateTime updatedAt;

    public static class OrderItem {
        public String productId;
        public int quantity;
        public BigDecimal price;
    }
}