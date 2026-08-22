package com.lms.backend.repository;

import com.lms.backend.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByTeacherIdAndPublishedTrue(Long teacherId);
    List<Book> findByTeacherId(Long teacherId);
}