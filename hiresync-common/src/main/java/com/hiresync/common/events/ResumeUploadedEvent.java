package com.hiresync.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ResumeUploadedEvent implements KafkaEvent {
    private String eventType = "RESUME_UPLOADED";
    private String userId;
    private String resumeId;
    private String rawText;
    private String label;
}
