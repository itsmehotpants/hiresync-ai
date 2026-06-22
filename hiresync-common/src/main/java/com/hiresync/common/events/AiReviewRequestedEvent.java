package com.hiresync.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AiReviewRequestedEvent implements KafkaEvent {
    private String eventType = "AI_REVIEW_REQUESTED";
    private String userId;
    private String jobApplicationId;
    private String resumeId;
}
