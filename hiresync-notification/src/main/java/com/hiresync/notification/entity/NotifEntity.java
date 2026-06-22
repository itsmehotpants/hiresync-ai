package com.hiresync.notification.entity;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
public class NotifEntity {
    @Id
    @Column(length = 26)
    private String id;

    @Column(nullable = false, length = 26)
    private String userId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    private String notificationType;
    private boolean isRead = false;
    private String relatedEntityId;

    @CreationTimestamp
    private OffsetDateTime createdAt;

    @PrePersist
    protected void prePersist() {
        if (this.id == null) this.id = UlidCreator.getUlid().toString();
    }
}
