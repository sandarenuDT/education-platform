package com.lms.backend.dto.request;

import com.lms.backend.util.UserValidationRules;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// Used by a SUPER_ADMIN to create a new teacher account (User + Teacher
// profile together). There is no public/self-service equivalent of this —
// see DataSeeder for why, and SuperAdminTeacherController for where this is used.
@Getter
@Setter
public class CreateTeacherRequest {

    @NotBlank(message = UserValidationRules.NAME_REQUIRED)
    private String name;

    @NotBlank(message = UserValidationRules.EMAIL_REQUIRED)
    @Email(message = UserValidationRules.EMAIL_INVALID)
    private String email;

    @NotBlank(message = UserValidationRules.PASSWORD_REQUIRED)
    @Size(min = UserValidationRules.PASSWORD_MIN_LENGTH, message = UserValidationRules.PASSWORD_SIZE)
    private String password;

    @NotBlank(message = "Slug is required")
    private String slug;

    @NotBlank(message = "Display name is required")
    private String displayName;

    private String bio;
    private String subjectArea;
    private String photoUrl;
}