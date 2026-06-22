package com.hiresync.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class JobApplicationCreatedEvent implements KafkaEvent {
    private String eventType = "JOB_CREATED";
    private String userId;
    private String jobApplicationId;
    private String companyName;
    private String roleTitle;
    private String status;
}
