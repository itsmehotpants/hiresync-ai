package com.hiresync.core.kafka;

import com.hiresync.common.events.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisher {

    private static final String JOB_CREATED = "job.created";
    private static final String JOB_STATUS_CHANGED = "job.status.changed";
    private static final String RESUME_UPLOADED = "resume.uploaded";
    private static final String AI_REVIEW_REQUESTED = "ai.review.requested";
    private static final String NOTIFICATION_SEND = "notification.send";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishJobCreated(JobApplicationCreatedEvent event) {
        send(JOB_CREATED, event.getUserId(), event);
    }

    public void publishStatusChanged(JobStatusChangedEvent event) {
        send(JOB_STATUS_CHANGED, event.getUserId(), event);
    }

    public void publishResumeUploaded(ResumeUploadedEvent event) {
        send(RESUME_UPLOADED, event.getUserId(), event);
    }

    public void publishAiReviewRequested(AiReviewRequestedEvent event) {
        send(AI_REVIEW_REQUESTED, event.getUserId(), event);
    }

    public void publishNotification(NotificationSendEvent event) {
        send(NOTIFICATION_SEND, event.getUserId(), event);
    }

    private void send(String topic, String key, Object payload) {
        kafkaTemplate.send(topic, key, payload)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish to topic {}: {}", topic, ex.getMessage());
                    } else {
                        log.debug("Published to {}, partition {}, offset {}",
                                topic,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
