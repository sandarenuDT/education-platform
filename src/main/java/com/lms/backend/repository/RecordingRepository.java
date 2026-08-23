package com.lms.backend.repository;

import com.lms.backend.model.Recording;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecordingRepository extends JpaRepository<Recording, Long> {
    List<Recording> findByTeacherIdAndPublishedTrue(Long teacherId);
    List<Recording> findByTeacherId(Long teacherId);
    List<Recording> findByPublishedTrue();

}