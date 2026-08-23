package com.lms.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

// Public-facing shape of a Book. Deliberately excludes fileUrl — the real
// private storage link is NEVER sent here; it's only resolved to a signed,
// short-lived URL later, after AccessControlService confirms purchase.
@Getter
@AllArgsConstructor
public class BookResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String coverImageUrl;
    private BigDecimal price;
    private boolean free;
    private String teacherName;
    private String teacherSlug;
}