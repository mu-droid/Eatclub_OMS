package com.eatclub.assignment.DTO;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public class OrderRequest {
    @NotBlank
    public String customerId;

    @NotNull
    @Size(min = 1)
    public List<OrderItem> items;

    public static class OrderItem {
        @NotBlank
        public String productId;
        @Min(1)
        public int quantity;
        @NotNull
        @DecimalMin("0.0")
        public BigDecimal price;
    }
}
