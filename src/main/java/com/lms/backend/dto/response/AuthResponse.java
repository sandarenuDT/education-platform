package com.lms.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Never expose the User model directly — this is the deliberate boundary
// between the DB shape and what the frontend actually receives.
@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String name;
    private String email;
    private String role;
}