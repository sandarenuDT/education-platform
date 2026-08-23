package com.lms.backend.controller.teacherAdmin;

import com.lms.backend.dto.request.CreateRecordingRequest;
import com.lms.backend.dto.response.RecordingResponse;
import com.lms.backend.security.CustomUserDetails;
import com.lms.backend.service.RecordingService;
import com.lms.backend.util.ApiPaths;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.TEACHER_ADMIN_RECORDINGS)
@PreAuthorize("hasRole('TEACHER_ADMIN')")
public class TeacherAdminRecordingController {

    private final RecordingService recordingService;

    @Autowired
    public TeacherAdminRecordingController(RecordingService recordingService) {
        this.recordingService = recordingService;
    }

    @PostMapping
    public ResponseEntity<RecordingResponse> createRecording(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody CreateRecordingRequest request
    ) {
        RecordingResponse response = recordingService.createRecording(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<RecordingResponse>> getMyRecordings(@AuthenticationPrincipal CustomUserDetails principal) {
        return ResponseEntity.ok(recordingService.getMyRecordings(principal.getId()));
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<RecordingResponse> publishRecording(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(recordingService.publishRecording(principal.getId(), id));
    }
}