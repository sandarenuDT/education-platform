package com.lms.backend.dto.request;

import com.lms.backend.util.UserValidationRules;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = UserValidationRules.EMAIL_REQUIRED)
    @Email(message = UserValidationRules.EMAIL_INVALID)
    private String email;

    @NotBlank(message = UserValidationRules.PASSWORD_REQUIRED)
    private String password;
}