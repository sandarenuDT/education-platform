package com.lms.backend.controller.student;

import com.lms.backend.exception.ForbiddenException;
import com.lms.backend.exception.ResourceNotFoundException;
import com.lms.backend.model.Book;
import com.lms.backend.model.enums.ItemType;
import com.lms.backend.repository.BookRepository;
import com.lms.backend.security.CustomUserDetails;
import com.lms.backend.service.AccessControlService;
import com.lms.backend.util.ApiPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * THIS is the endpoint that makes the whole platform's core rule real: paid
 * content is invisible until purchased. Every future protected endpoint
 * (recording stream URLs, etc.) follows this exact same shape:
 *   1. Who is asking? (@AuthenticationPrincipal)
 *   2. Does the item exist?
 *   3. Does AccessControlService say they can have it?
 *   4. Only then, return the actual content/link.
 *
 * NOTE: this currently returns the raw fileUrl. A real implementation should
 * never do that — it should call a StorageService that returns a short-lived
 * SIGNED url instead, so the link itself expires. That's a later hardening
 * step (Step 9/10 territory); this proves the access-gating logic first.
 */
@RestController
@RequestMapping(ApiPaths.STUDENT_BOOKS)
public class StudentBookAccessController {

    private final BookRepository bookRepository;
    private final AccessControlService accessControlService;

    @Autowired
    public StudentBookAccessController(BookRepository bookRepository, AccessControlService accessControlService) {
        this.bookRepository = bookRepository;
        this.accessControlService = accessControlService;
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<Map<String, String>> getBookContent(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long id
    ) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        boolean allowed = book.isFree()
                || accessControlService.hasAccess(principal.getId(), ItemType.BOOK, id);

        if (!allowed) {
            throw new ForbiddenException("Purchase required to access this book");
        }

        // HashMap (not Map.of) because fileUrl can legitimately be null if the
        // teacher hasn't uploaded a file yet — Map.of() throws NPE on any null
        // value, which is exactly what happened here.
        Map<String, String> response = new HashMap<>();
        response.put("fileUrl", book.getFileUrl());
        return ResponseEntity.ok(response);
    }
}