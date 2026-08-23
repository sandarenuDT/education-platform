package com.lms.backend.controller.teacherAdmin;

import com.lms.backend.dto.request.CreateBookRequest;
import com.lms.backend.dto.response.BookResponse;
import com.lms.backend.security.CustomUserDetails;
import com.lms.backend.service.BookService;
import com.lms.backend.util.ApiPaths;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Every endpoint here requires a valid JWT belonging to a TEACHER_ADMIN.
// @AuthenticationPrincipal injects the CustomUserDetails that JwtAuthFilter
// placed into the security context — from there we resolve which Teacher
// profile this specific logged-in user owns (see BookService.resolveTeacherForUser).
@RestController
@RequestMapping(ApiPaths.TEACHER_ADMIN_BOOKS)
@PreAuthorize("hasRole('TEACHER_ADMIN')")
public class TeacherAdminBookController {

    private final BookService bookService;

    @Autowired
    public TeacherAdminBookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PostMapping
    public ResponseEntity<BookResponse> createBook(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody CreateBookRequest request
    ) {
        BookResponse response = bookService.createBook(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<BookResponse>> getMyBooks(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(bookService.getMyBooks(principal.getId()));
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<BookResponse> publishBook(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(bookService.publishBook(principal.getId(), id));
    }
}