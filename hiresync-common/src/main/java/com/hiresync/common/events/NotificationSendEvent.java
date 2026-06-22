package com.hiresync.common.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class NotificationSendEvent implements KafkaEvent {
    private String eventType = "NOTIFICATION_SEND";
    private String userId;
    private String notificationType;  // WELCOME, GHOST_ALERT, OFFER_RECEIVED, etc.
    private String title;
    private String body;
    private String relatedEntityId;
    private boolean sendEmail;
    private String recipientEmail;
}
