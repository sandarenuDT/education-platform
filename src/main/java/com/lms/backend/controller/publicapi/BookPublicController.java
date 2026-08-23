package com.lms.backend.controller.publicapi;

import com.lms.backend.dto.response.BookResponse;
import com.lms.backend.service.BookService;
import com.lms.backend.util.ApiPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// No auth required — this is the public catalog anyone can browse.
// Actual paid content (fileUrl) is never exposed here; see BookResponse.
@RestController
@RequestMapping(ApiPaths.PUBLIC_BOOKS)
public class BookPublicController {

    private final BookService bookService;

    @Autowired
    public BookPublicController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> listPublishedBooks() {
        return ResponseEntity.ok(bookService.getPublishedBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getPublishedBookById(id));
    }
}