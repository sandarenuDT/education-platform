package com.lms.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeacherResponse {
    private Long id;
    private String slug;
    private String displayName;
    private String bio;
    private String subjectArea;
    private String photoUrl;
    private boolean approved;
    private String teacherEmail;
}