package com.lms.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateRecordingRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private String category;
    private String thumbnailUrl;
    private String videoProvider;
    private String videoProviderId;
    private Integer durationSeconds;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price cannot be negative")
    private BigDecimal price;
}