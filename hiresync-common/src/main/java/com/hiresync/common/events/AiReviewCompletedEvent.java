package com.hiresync.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AiReviewCompletedEvent implements KafkaEvent {
    private String eventType = "AI_REVIEW_COMPLETED";
    private String userId;
    private String jobApplicationId;
    private Integer matchScore;
    private String aiFeedback;
    private String atsPrediction;
}
