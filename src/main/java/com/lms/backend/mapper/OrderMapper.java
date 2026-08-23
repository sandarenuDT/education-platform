package com.lms.backend.mapper;

import com.lms.backend.dto.response.OrderResponse;
import com.lms.backend.model.Order;

public final class OrderMapper {

    private OrderMapper() {
    }

    public static OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getItemType(),
                order.getItemId(),
                order.getAmount(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}