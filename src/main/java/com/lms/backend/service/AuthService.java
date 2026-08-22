package com.lms.backend.service;

import com.lms.backend.dto.request.LoginRequest;
import com.lms.backend.dto.request.RegisterRequest;
import com.lms.backend.dto.response.AuthResponse;
import com.lms.backend.exception.ConflictException;
import com.lms.backend.model.Teacher;
import com.lms.backend.model.User;
import com.lms.backend.model.enums.Role;
import com.lms.backend.repository.TeacherRepository;
import com.lms.backend.repository.UserRepository;
import com.lms.backend.security.JwtUtil;
import com.lms.backend.util.CommonMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(UserRepository userRepository,
                       TeacherRepository teacherRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Public self-registration always creates a STUDENT account. TEACHER_ADMIN
     * accounts are created separately (invited/approved by a SUPER_ADMIN) —
     * never through this open endpoint.
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException(CommonMessages.ACCOUNT_ALREADY_EXISTS);
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.STUDENT);

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name(), null);

        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found — this should not happen"));

        Long teacherId = null;
        if (user.getRole() == Role.TEACHER_ADMIN) {
            Optional<Teacher> teacher = teacherRepository.findByUserId(user.getId());
            if (teacher.isPresent()) {
                teacherId = teacher.get().getId();
            }
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name(), teacherId);

        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole().name());
    }
}