package com.hiresync.notification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "job_applications")
@Getter
@Setter
public class JobAppEntity {
    @Id
    private String id;
    private String userId;
    private String companyName;
    private String roleTitle;
    private String status;
    private String source;
    private LocalDate appliedAt;
    private Integer aiMatchScore;
    private OffsetDateTime createdAt;
}
