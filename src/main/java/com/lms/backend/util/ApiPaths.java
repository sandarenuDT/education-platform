package com.lms.backend.util;

/**
 * Central registry of API route prefixes. Referenced from both @RequestMapping
 * on controllers AND SecurityConfig's permitAll list — so a path only ever
 * needs to change in one place, and the two can never silently drift apart.
 *
 * NOTE: these are relative to server.servlet.context-path (set to "/api" in
 * application.yml) — Spring Security matches against the path AFTER the
 * context path is stripped, so these constants stay unprefixed.
 */
public final class ApiPaths {

    private ApiPaths() {
    }

    // Base segments
    public static final String AUTH = "/auth";
    public static final String PUBLIC = "/public";
    public static final String STUDENT = "/student";
    public static final String TEACHER_ADMIN = "/teacher-admin";
    public static final String SUPER_ADMIN = "/super-admin";

    // Wildcard versions, for SecurityConfig's requestMatchers
    public static final String AUTH_ALL = AUTH + "/**";
    public static final String PUBLIC_ALL = PUBLIC + "/**";

    // Public catalog sub-paths
    public static final String PUBLIC_BOOKS = PUBLIC + "/books";

    // Super-admin sub-paths
    public static final String SUPER_ADMIN_TEACHERS = SUPER_ADMIN + "/teachers";

}