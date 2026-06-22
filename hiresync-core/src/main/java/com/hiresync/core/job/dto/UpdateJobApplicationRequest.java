package com.hiresync.core.job.dto;

import com.hiresync.core.entity.JobApplicationStatus;

import java.time.LocalDate;

public record UpdateJobApplicationRequest(
        String companyName,
        String roleTitle,
        String jobUrl,
        String jobDescription,
        JobApplicationStatus status,
        String source,
        String location,
        Boolean isRemote,
        Long salaryMin,
        Long salaryMax,
        String salaryCurrency,
        String notes,
        LocalDate appliedAt,
        LocalDate deadlineAt
) {}
