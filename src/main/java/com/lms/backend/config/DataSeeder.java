package com.lms.backend.config;

import com.lms.backend.model.User;
import com.lms.backend.model.enums.Role;
import com.lms.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Solves the chicken-and-egg problem: only a SUPER_ADMIN can create a
 * TEACHER_ADMIN, but nothing can create the first SUPER_ADMIN through the API
 * (there's no open registration for that role, on purpose). On every startup,
 * this creates exactly one bootstrap SUPER_ADMIN — but only if none exists yet.
 *
 * IMPORTANT: change SUPER_ADMIN_PASSWORD (env var) in any real environment —
 * the default here is for local dev only.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String superAdminEmail;
    private final String superAdminPassword;

    public DataSeeder(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      @Value("${app.bootstrap.super-admin-email:admin@lms.local}") String superAdminEmail,
                      @Value("${app.bootstrap.super-admin-password:ChangeMe123!}") String superAdminPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.superAdminEmail = superAdminEmail;
        this.superAdminPassword = superAdminPassword;
    }

    @Override
    public void run(String... args) {
        boolean superAdminExists = !userRepository.findByRole(Role.SUPER_ADMIN).isEmpty();

        if (superAdminExists) {
            return;
        }

        User admin = new User();
        admin.setName("Platform Admin");
        admin.setEmail(superAdminEmail);
        admin.setPasswordHash(passwordEncoder.encode(superAdminPassword));
        admin.setRole(Role.SUPER_ADMIN);
        userRepository.save(admin);

        System.out.println("=================================================");
        System.out.println("Bootstrap SUPER_ADMIN created: " + superAdminEmail);
        System.out.println("Log in with this account, then change the password.");
        System.out.println("=================================================");
    }
}