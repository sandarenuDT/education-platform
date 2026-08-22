package com.lms.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

// General-purpose user representation — used for "my profile", admin user
// listings, or any nested reference to a user inside another response.
// No token field and no passwordHash, unlike AuthResponse — safe to reuse anywhere.
@Getter
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private boolean enabled;
    private Date createdAt;
}