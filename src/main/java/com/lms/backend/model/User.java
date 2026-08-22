package com.lms.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lms.backend.model.enums.Role;
import com.lms.backend.util.UserValidationRules;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.Date;

@Entity

@Table(name = "app_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = UserValidationRules.NAME_REQUIRED)
    @Size(min = UserValidationRules.NAME_MIN_LENGTH, max = UserValidationRules.NAME_MAX_LENGTH,
            message = UserValidationRules.NAME_SIZE)
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @NotNull(message = UserValidationRules.EMAIL_REQUIRED)
    @Email(message = UserValidationRules.EMAIL_INVALID)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // Never serialized back to the client — this is the whole point of a DTO boundary,
    // but @JsonIgnore is kept here too as a defense-in-depth safety net.
    @JsonIgnore
    @NotNull
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private Role role;

    @Column(name = "is_enabled", nullable = false)
    private boolean enabled = true;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    public User() {
    }

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

    // ---- Getters and setters ----

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}