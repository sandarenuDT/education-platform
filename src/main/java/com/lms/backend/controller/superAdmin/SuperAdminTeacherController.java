package com.lms.backend.controller.superAdmin;

import com.lms.backend.dto.request.CreateTeacherRequest;
import com.lms.backend.dto.response.TeacherResponse;
import com.lms.backend.service.TeacherService;
import com.lms.backend.util.ApiPaths;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Every endpoint here requires a valid JWT belonging to a SUPER_ADMIN.
// @PreAuthorize checks the role embedded in the token (see JwtUtil) against
// the authorities CustomUserDetails exposes — no separate DB role lookup needed.
@RestController
@RequestMapping(ApiPaths.SUPER_ADMIN_TEACHERS)
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class SuperAdminTeacherController {

    private final TeacherService teacherService;

    @Autowired
    public SuperAdminTeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping
    public ResponseEntity<TeacherResponse> createTeacher(@Valid @RequestBody CreateTeacherRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teacherService.createTeacher(request));
    }
}