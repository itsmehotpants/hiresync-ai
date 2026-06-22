package com.hiresync.core.job.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateJobApplicationRequest(
        @NotBlank @Size(max = 255) String companyName,
        @NotBlank @Size(max = 255) String roleTitle,
        String jobUrl,
        String jobDescription,
        String source,
        String location,
        boolean isRemote,
        Long salaryMin,
        Long salaryMax,
        String salaryCurrency,
        String notes,
        LocalDate appliedAt,
        LocalDate deadlineAt
) {}
