package com.hiresync.core.job.dto;

import com.hiresync.core.entity.JobApplicationStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record JobApplicationDTO(
        String id,
        String companyName,
        String roleTitle,
        String jobUrl,
        String jobDescription,
        JobApplicationStatus status,
        int stageOrder,
        String source,
        String location,
        boolean isRemote,
        Long salaryMin,
        Long salaryMax,
        String salaryCurrency,
        String notes,
        LocalDate appliedAt,
        LocalDate deadlineAt,
        Integer aiMatchScore,
        String aiFeedback,
        String atsPrediction,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
