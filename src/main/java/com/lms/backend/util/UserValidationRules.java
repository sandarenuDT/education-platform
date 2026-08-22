package com.lms.backend.util;

public class UserValidationRules {

    private UserValidationRules(){
    }

    public static final int NAME_MIN_LENGTH = 2;
    public static final int NAME_MAX_LENGTH = 150;
    public static final String NAME_REQUIRED = "Name is required";
    public static final String NAME_SIZE = "Name must be between " + NAME_MIN_LENGTH + " and " + NAME_MAX_LENGTH + " characters";

    public static final String EMAIL_REQUIRED = "Email is required";
    public static final String EMAIL_INVALID = "Email must be a valid address";

    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final String PASSWORD_REQUIRED = "Password is required";
    public static final String PASSWORD_SIZE = "Password must be at least " + PASSWORD_MIN_LENGTH + " characters";

    // Local phone-number style pattern (adjust to your country's format).
    public static final String PHONE_PATTERN = "^[0-9+\\-\\s]{7,15}$";
    public static final String PHONE_INVALID = "Phone number format is invalid";
}
