package com.lms.backend.mapper;

import com.lms.backend.dto.response.TeacherResponse;
import com.lms.backend.model.Teacher;

public final class TeacherMapper {

    private TeacherMapper() {
    }

    public static TeacherResponse toResponse(Teacher teacher) {
        return new TeacherResponse(
                teacher.getId(),
                teacher.getSlug(),
                teacher.getDisplayName(),
                teacher.getBio(),
                teacher.getSubjectArea(),
                teacher.getPhotoUrl(),
                teacher.isApproved(),
                teacher.getUser().getEmail()
        );
    }
}