package com.hiresync.ai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "interview_rounds")
@Getter
@Setter
public class AiInterviewRound {
    @Id
    private String id;
    private String jobApplicationId;
    private int roundNumber;
    private String roundType;
    private OffsetDateTime scheduledAt;
    private Integer durationMinutes;
    private String outcome;
    private OffsetDateTime createdAt;
}
