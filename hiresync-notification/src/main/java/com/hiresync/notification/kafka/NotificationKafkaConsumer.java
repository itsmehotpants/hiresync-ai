package com.hiresync.notification.kafka;

import com.hiresync.common.events.*;
import com.hiresync.notification.email.EmailService;
import com.hiresync.notification.inapp.InAppNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationKafkaConsumer {

    private final EmailService emailService;
    private final InAppNotificationService inAppNotificationService;

    @KafkaListener(topics = "notification.send", groupId = "notification-service-group")
    public void onNotificationSend(KafkaEvent event) {
        if (!(event instanceof NotificationSendEvent e)) return;
        log.info("Processing notification: type={}, userId={}", e.getNotificationType(), e.getUserId());

        // Save in-app notification
        inAppNotificationService.create(e.getUserId(), e.getTitle(), e.getBody(),
                e.getNotificationType(), e.getRelatedEntityId());

        // Send email if requested
        if (e.isSendEmail() && e.getRecipientEmail() != null) {
            String htmlBody = switch (e.getNotificationType()) {
                case "OFFER_RECEIVED" -> emailService.buildOfferEmail(
                        e.getTitle(), "Company", "Role");
                case "GHOST_ALERT" -> emailService.buildGhostAlertEmail(
                        "Job Seeker", 1);
                default -> "<p>" + e.getBody() + "</p>";
            };
            emailService.sendHtmlEmail(e.getRecipientEmail(), e.getTitle(), htmlBody);
        }
    }

    @KafkaListener(topics = "job.created", groupId = "notification-service-group")
    public void onJobCreated(KafkaEvent event) {
        if (!(event instanceof JobApplicationCreatedEvent e)) return;
        log.debug("Job created event received for analytics: jobId={}", e.getJobApplicationId());
        // Analytics processing would go here
    }

    @KafkaListener(topics = "job.status.changed", groupId = "notification-service-group")
    public void onStatusChanged(KafkaEvent event) {
        if (!(event instanceof JobStatusChangedEvent e)) return;
        log.debug("Status changed: {} -> {} for job {}", e.getOldStatus(), e.getNewStatus(), e.getJobApplicationId());
    }
}
