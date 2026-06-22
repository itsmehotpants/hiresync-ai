package com.hiresync.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class JobStatusChangedEvent implements KafkaEvent {
    private String eventType = "JOB_STATUS_CHANGED";
    private String userId;
    private String jobApplicationId;
    private String companyName;
    private String roleTitle;
    private String oldStatus;
    private String newStatus;
}
