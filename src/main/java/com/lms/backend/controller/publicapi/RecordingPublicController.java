package com.lms.backend.controller.publicapi;

import com.lms.backend.dto.response.RecordingResponse;
import com.lms.backend.service.RecordingService;
import com.lms.backend.util.ApiPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// No auth required — public catalog only. The actual video stream URL is
// NEVER exposed here; that comes from a future protected endpoint gated by
// AccessControlService, once purchases are wired up.
@RestController
@RequestMapping(ApiPaths.PUBLIC_RECORDINGS)
public class RecordingPublicController {

    private final RecordingService recordingService;

    @Autowired
    public RecordingPublicController(RecordingService recordingService) {
        this.recordingService = recordingService;
    }

    @GetMapping
    public ResponseEntity<List<RecordingResponse>> listPublishedRecordings() {
        return ResponseEntity.ok(recordingService.getPublishedRecordings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecordingResponse> getRecording(@PathVariable Long id) {
        return ResponseEntity.ok(recordingService.getPublishedRecordingById(id));
    }
}