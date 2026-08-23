package com.lms.backend.mapper;

import com.lms.backend.dto.response.BookResponse;
import com.lms.backend.model.Book;

/**
 * The one place that knows how to convert Book -> BookResponse.
 * Notice fileUrl is never touched here — that's intentional (see BookResponse).
 */
public final class BookMapper {

    private BookMapper() {
    }

    public static BookResponse toResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getDescription(),
                book.getCategory(),
                book.getCoverImageUrl(),
                book.getPrice(),
                book.isFree(),
                book.getTeacher().getDisplayName(),
                book.getTeacher().getSlug()
        );
    }
}