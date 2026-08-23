package com.lms.backend.dto.request;

import com.lms.backend.model.enums.ItemType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {

    @NotNull(message = "itemType is required")
    private ItemType itemType;

    @NotNull(message = "itemId is required")
    private Long itemId;
}