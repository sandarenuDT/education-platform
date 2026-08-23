package com.lms.backend.service;

import com.lms.backend.dto.request.CreateBookRequest;
import com.lms.backend.dto.response.BookResponse;
import com.lms.backend.exception.ForbiddenException;
import com.lms.backend.exception.ResourceNotFoundException;
import com.lms.backend.mapper.BookMapper;
import com.lms.backend.model.Book;
import com.lms.backend.model.Teacher;
import com.lms.backend.repository.BookRepository;
import com.lms.backend.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final TeacherRepository teacherRepository;


    @Autowired
    public BookService(BookRepository bookRepository, TeacherRepository teacherRepository) {
        this.bookRepository = bookRepository;
        this.teacherRepository = teacherRepository;

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

    // --- Teacher-admin: manage own books ---

    // Resolves which Teacher profile the logged-in user owns. Throws if the
    // account has TEACHER_ADMIN role but somehow has no Teacher row — this
    // should never happen given TeacherService always creates them together,
    // but we don't silently proceed with a null teacher if it ever does.
    private Teacher resolveTeacherForUser(Long userId) {
        return teacherRepository.findByUserId(userId)
                .orElseThrow(() -> new ForbiddenException("No teacher profile found for this account"));
    }

    public BookResponse createBook(Long userId, CreateBookRequest request) {
        Teacher teacher = resolveTeacherForUser(userId);

        Book book = new Book();
        book.setTeacher(teacher);
        book.setTitle(request.getTitle());
        book.setDescription(request.getDescription());
        book.setCategory(request.getCategory());
        book.setCoverImageUrl(request.getCoverImageUrl());
        book.setFileUrl(request.getFileUrl());
        book.setPrice(request.getPrice());
        book.setFree(request.isFree());
        book.setPublished(false); // always starts unpublished — teacher publishes explicitly

        book = bookRepository.save(book);
        return BookMapper.toResponse(book);
    }

    // Includes unpublished/draft books — this is the teacher's own view of
    // their content, unlike the public catalog which only shows published ones.
    public List<BookResponse> getMyBooks(Long userId) {
        Teacher teacher = resolveTeacherForUser(userId);
        return bookRepository.findByTeacherId(teacher.getId())
                .stream()
                .map(BookMapper::toResponse)
                .toList();
    }

    public BookResponse publishBook(Long userId, Long bookId) {
        Teacher teacher = resolveTeacherForUser(userId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        // A teacher can only publish their OWN books — this is the actual
        // multi-tenancy check, not just an auth check. Without this line, any
        // logged-in teacher could publish/expose another teacher's draft.
        if (!book.getTeacher().getId().equals(teacher.getId())) {
            throw new ForbiddenException("You do not have access to this book");
        }

        book.setPublished(true);
        book = bookRepository.save(book);
        return BookMapper.toResponse(book);
    }
}