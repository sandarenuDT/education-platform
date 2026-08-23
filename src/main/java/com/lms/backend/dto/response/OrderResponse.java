package com.lms.backend.dto.response;

import com.lms.backend.model.enums.ItemType;
import com.lms.backend.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private ItemType itemType;
    private Long itemId;
    private BigDecimal amount;
    private OrderStatus status;
    private Date createdAt;
}