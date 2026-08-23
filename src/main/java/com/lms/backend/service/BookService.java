package com.lms.backend.service;

import com.lms.backend.dto.response.BookResponse;
import com.lms.backend.exception.ResourceNotFoundException;
import com.lms.backend.mapper.BookMapper;
import com.lms.backend.model.Book;
import com.lms.backend.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Public catalog — only ever returns published books. Unpublished/draft
    // books stay invisible until the teacher explicitly publishes them
    // (that toggle lives in the teacher-admin endpoints, built later).
    public List<BookResponse> getPublishedBooks() {
        return bookRepository.findByPublishedTrue()
                .stream()
                .map(BookMapper::toResponse)
                .toList();
    }

    public BookResponse getPublishedBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        if (!book.isPublished()) {
            // Deliberately the same "not found" message as a missing book —
            // an unpublished book shouldn't reveal its existence to the public.
            throw new ResourceNotFoundException("Book not found");
        }

        return BookMapper.toResponse(book);
    }
}