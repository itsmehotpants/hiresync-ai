package com.hiresync.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "timeline_events")
@Getter
@Setter
public class TimelineEvent {

    @Id
    @Column(length = 26, updatable = false, nullable = false)
    private String id;

    @Column(nullable = false, length = 26)
    private String jobApplicationId;

    @Column(nullable = false)
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String description;

    private OffsetDateTime occurredAt = OffsetDateTime.now();

    @PrePersist
    protected void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = com.github.f4b6a3.ulid.UlidCreator.getUlid().toString();
        }
    }
}
