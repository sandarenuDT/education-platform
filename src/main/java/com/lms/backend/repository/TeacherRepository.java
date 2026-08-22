package com.lms.backend.repository;

import com.lms.backend.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findBySlug(String slug);
    Optional<Teacher> findByUserId(Long userId);
    boolean existsBySlug(String slug);
}