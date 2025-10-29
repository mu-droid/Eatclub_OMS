package com.eatclub.assignment.DTO;

import com.eatclub.assignment.Entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateStatusRequest {
    @NotNull
    public OrderStatus status;
}