package com.lms.backend.service;

import com.lms.backend.dto.request.CreateRecordingRequest;
import com.lms.backend.dto.response.RecordingResponse;
import com.lms.backend.exception.ForbiddenException;
import com.lms.backend.exception.ResourceNotFoundException;
import com.lms.backend.mapper.RecordingMapper;
import com.lms.backend.model.Recording;
import com.lms.backend.model.Teacher;
import com.lms.backend.repository.RecordingRepository;
import com.lms.backend.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecordingService {

    private final RecordingRepository recordingRepository;
    private final TeacherRepository teacherRepository;

    @Autowired
    public RecordingService(RecordingRepository recordingRepository, TeacherRepository teacherRepository) {
        this.recordingRepository = recordingRepository;
        this.teacherRepository = teacherRepository;
    }

    // --- Public catalog ---

    public List<RecordingResponse> getPublishedRecordings() {
        return recordingRepository.findByPublishedTrue()
                .stream()
                .map(RecordingMapper::toResponse)
                .toList();
    }

    public RecordingResponse getPublishedRecordingById(Long id) {
        Recording recording = recordingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recording not found"));

        if (!recording.isPublished()) {
            throw new ResourceNotFoundException("Recording not found");
        }

        return RecordingMapper.toResponse(recording);
    }

    // --- Teacher-admin: manage own recordings ---

    private Teacher resolveTeacherForUser(Long userId) {
        return teacherRepository.findByUserId(userId)
                .orElseThrow(() -> new ForbiddenException("No teacher profile found for this account"));
    }

    public RecordingResponse createRecording(Long userId, CreateRecordingRequest request) {
        Teacher teacher = resolveTeacherForUser(userId);

        Recording recording = new Recording();
        recording.setTeacher(teacher);
        recording.setTitle(request.getTitle());
        recording.setDescription(request.getDescription());
        recording.setCategory(request.getCategory());
        recording.setThumbnailUrl(request.getThumbnailUrl());
        recording.setVideoProvider(request.getVideoProvider());
        recording.setVideoProviderId(request.getVideoProviderId());
        recording.setDurationSeconds(request.getDurationSeconds());
        recording.setPrice(request.getPrice());
        recording.setPublished(false);

        recording = recordingRepository.save(recording);
        return RecordingMapper.toResponse(recording);
    }

    public List<RecordingResponse> getMyRecordings(Long userId) {
        Teacher teacher = resolveTeacherForUser(userId);
        return recordingRepository.findByTeacherId(teacher.getId())
                .stream()
                .map(RecordingMapper::toResponse)
                .toList();
    }

    public RecordingResponse publishRecording(Long userId, Long recordingId) {
        Teacher teacher = resolveTeacherForUser(userId);

        Recording recording = recordingRepository.findById(recordingId)
                .orElseThrow(() -> new ResourceNotFoundException("Recording not found"));

        if (!recording.getTeacher().getId().equals(teacher.getId())) {
            throw new ForbiddenException("You do not have access to this recording");
        }

        recording.setPublished(true);
        recording = recordingRepository.save(recording);
        return RecordingMapper.toResponse(recording);
    }
}