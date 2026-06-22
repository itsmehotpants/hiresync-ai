package com.hiresync.notification.inapp;

import com.hiresync.notification.entity.NotifEntity;
import com.hiresync.notification.repository.NotifRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InAppNotificationService {

    private final NotifRepository notifRepository;

    public void create(String userId, String title, String body, String type, String relatedEntityId) {
        NotifEntity n = new NotifEntity();
        n.setUserId(userId);
        n.setTitle(title);
        n.setBody(body);
        n.setNotificationType(type);
        n.setRelatedEntityId(relatedEntityId);
        notifRepository.save(n);
        log.debug("In-app notification created for user {}: {}", userId, title);
    }

    @Transactional(readOnly = true)
    public List<NotifEntity> getNotifications(String userId) {
        return notifRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String userId) {
        return notifRepository.countUnreadByUserId(userId);
    }

    public void markRead(String notificationId, String userId) {
        notifRepository.findByIdAndUserId(notificationId, userId).ifPresent(n -> {
            n.setRead(true);
            notifRepository.save(n);
        });
    }

    public void markAllRead(String userId) {
        notifRepository.markAllReadForUser(userId);
    }
}
