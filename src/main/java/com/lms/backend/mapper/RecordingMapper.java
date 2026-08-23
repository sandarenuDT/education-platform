package com.lms.backend.mapper;

import com.lms.backend.dto.response.RecordingResponse;
import com.lms.backend.model.Recording;

public final class RecordingMapper {

    private RecordingMapper() {
    }

    public static RecordingResponse toResponse(Recording recording) {
        return new RecordingResponse(
                recording.getId(),
                recording.getTitle(),
                recording.getDescription(),
                recording.getCategory(),
                recording.getThumbnailUrl(),
                recording.getDurationSeconds(),
                recording.getPrice(),
                recording.getTeacher().getDisplayName(),
                recording.getTeacher().getSlug()
        );
    }
}