package com.lms.backend.dto.request;

import com.lms.backend.util.UserValidationRules;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = UserValidationRules.NAME_REQUIRED)
    private String name;

    @NotBlank(message = UserValidationRules.EMAIL_REQUIRED)
    @Email(message = UserValidationRules.EMAIL_INVALID)
    private String email;

    @NotBlank(message = UserValidationRules.PASSWORD_REQUIRED)
    @Size(min = UserValidationRules.PASSWORD_MIN_LENGTH, message = UserValidationRules.PASSWORD_SIZE)
    private String password;
}