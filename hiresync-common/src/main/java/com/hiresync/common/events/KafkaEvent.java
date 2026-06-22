package com.hiresync.common.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Base marker interface for all Kafka domain events.
 * Jackson polymorphism handles type-safe deserialization without __TypeId__ headers.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "eventType")
@JsonSubTypes({
    @JsonSubTypes.Type(value = JobApplicationCreatedEvent.class, name = "JOB_CREATED"),
    @JsonSubTypes.Type(value = JobStatusChangedEvent.class, name = "JOB_STATUS_CHANGED"),
    @JsonSubTypes.Type(value = ResumeUploadedEvent.class, name = "RESUME_UPLOADED"),
    @JsonSubTypes.Type(value = AiReviewRequestedEvent.class, name = "AI_REVIEW_REQUESTED"),
    @JsonSubTypes.Type(value = AiReviewCompletedEvent.class, name = "AI_REVIEW_COMPLETED"),
    @JsonSubTypes.Type(value = NotificationSendEvent.class, name = "NOTIFICATION_SEND"),
})
public interface KafkaEvent {
    String getEventType();
    String getUserId();
}
