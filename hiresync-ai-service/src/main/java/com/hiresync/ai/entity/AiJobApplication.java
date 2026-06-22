package com.hiresync.ai.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Read-only view of job_applications table for AI service queries.
 * AI service only reads — writes are done by core service.
 */
@Entity
@Table(name = "job_applications")
@Getter
@Setter
public class AiJobApplication {
    @Id
    private String id;
    private String userId;
    private String companyName;
    private String roleTitle;
    private String status;
    private LocalDate appliedAt;
    private Integer aiMatchScore;
    private String atsPrediction;
    private int stageOrder;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
