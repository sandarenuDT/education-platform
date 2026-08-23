package com.lms.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

// Public-facing shape of a Recording. Deliberately excludes videoProviderId —
// that's the private key used to request a signed playback URL, never handed
// to the client directly. The actual stream URL only ever comes from a future
// protected endpoint that checks AccessControlService first.
@Getter
@AllArgsConstructor
public class RecordingResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String thumbnailUrl;
    private Integer durationSeconds;
    private BigDecimal price;
    private String teacherName;
    private String teacherSlug;
}