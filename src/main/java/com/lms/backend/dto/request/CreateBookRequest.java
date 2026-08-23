package com.lms.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// teacherId is deliberately NOT a field here — it's resolved server-side from
// the logged-in teacher's JWT, never trusted from client input. Otherwise a
// teacher could pass someone else's teacherId and publish under their name.
@Getter
@Setter
public class CreateBookRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private String category;
    private String coverImageUrl;
    private String fileUrl;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price cannot be negative")
    private BigDecimal price;

    private boolean free = false;
}