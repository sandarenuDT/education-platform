package com.lms.backend.service;
import com.lms.backend.dto.request.CreateTeacherRequest;
import com.lms.backend.dto.response.TeacherResponse;
import com.lms.backend.exception.ConflictException;
import com.lms.backend.mapper.TeacherMapper;
import com.lms.backend.model.Teacher;
import com.lms.backend.model.User;
import com.lms.backend.model.enums.Role;
import com.lms.backend.repository.TeacherRepository;
import com.lms.backend.repository.UserRepository;
import com.lms.backend.util.CommonMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeacherService {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TeacherService(UserRepository userRepository,
                          TeacherRepository teacherRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Only ever called from SuperAdminTeacherController — creates the login
    // account (role = TEACHER_ADMIN) and the public Teacher profile together,
    // since one is meaningless without the other.
    @Transactional
    public TeacherResponse createTeacher(CreateTeacherRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException(CommonMessages.ACCOUNT_ALREADY_EXISTS);
        }
        if (teacherRepository.existsBySlug(request.getSlug())) {
            throw new ConflictException("This slug is already taken");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.TEACHER_ADMIN);
        user = userRepository.save(user);

        Teacher teacher = new Teacher();
        teacher.setUser(user);
        teacher.setSlug(request.getSlug());
        teacher.setDisplayName(request.getDisplayName());
        teacher.setBio(request.getBio());
        teacher.setSubjectArea(request.getSubjectArea());
        teacher.setPhotoUrl(request.getPhotoUrl());
        teacher.setApproved(true); // created directly by a super-admin — pre-approved
        teacher = teacherRepository.save(teacher);

        return TeacherMapper.toResponse(teacher);
    }
}