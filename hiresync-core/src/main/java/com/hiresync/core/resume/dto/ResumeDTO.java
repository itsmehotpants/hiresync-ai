package com.hiresync.core.resume.dto;

import java.time.OffsetDateTime;

public record ResumeDTO(
        String id,
        String label,
        String fileName,
        Long fileSize,
        String contentType,
        boolean isPrimary,
        int versionNumber,
        OffsetDateTime createdAt
) {}
