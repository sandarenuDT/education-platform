package com.lms.backend.exception;

import java.time.Instant;
import java.util.Map;

// Every error the API returns uses this exact shape, so the frontend has
// ONE place that parses errors instead of guessing per-endpoint.
public record ApiError(
        int status,
        String code,
        String message,
        Instant timestamp,
        Map<String, String> fieldErrors
) {
    public static ApiError of(int status, String code, String message) {
        return new ApiError(status, code, message, Instant.now(), null);
    }

    public static ApiError ofValidation(Map<String, String> fieldErrors) {
        return new ApiError(400, "VALIDATION_ERROR", "One or more fields are invalid", Instant.now(), fieldErrors);
    }
}