package com.hiresync.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "interview_rounds")
@Getter
@Setter
public class InterviewRound extends BaseEntity {

    @Column(nullable = false, length = 26)
    private String jobApplicationId;

    private int roundNumber;
    private String roundType;
    private String interviewerName;
    private String interviewerRole;
    private OffsetDateTime scheduledAt;
    private Integer durationMinutes;

    @Column(columnDefinition = "TEXT")
    private String questionsAsked;

    @Column(columnDefinition = "TEXT")
    private String myAnswers;

    @Column(columnDefinition = "TEXT")
    private String feedbackReceived;

    private Integer selfRating;
    private String outcome;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
