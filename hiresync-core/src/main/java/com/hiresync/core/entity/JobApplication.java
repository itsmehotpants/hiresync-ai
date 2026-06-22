package com.hiresync.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_applications")
@Getter
@Setter
@ToString(exclude = {"interviewRounds", "timelineEvents"})
public class JobApplication extends BaseEntity {

    @Column(nullable = false, length = 26)
    private String userId;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String roleTitle;

    @Column(columnDefinition = "TEXT")
    private String jobUrl;

    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobApplicationStatus status = JobApplicationStatus.WISHLIST;

    private int stageOrder = 0;
    private String source;
    private String location;
    private boolean isRemote = false;
    private Long salaryMin;
    private Long salaryMax;
    private String salaryCurrency = "INR";

    @Column(columnDefinition = "TEXT")
    private String notes;

    private LocalDate appliedAt;
    private LocalDate deadlineAt;
    private Integer aiMatchScore;

    @Column(columnDefinition = "TEXT")
    private String aiFeedback;

    private String atsPrediction;

    @OneToMany(mappedBy = "jobApplicationId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterviewRound> interviewRounds = new ArrayList<>();

    @OneToMany(mappedBy = "jobApplicationId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TimelineEvent> timelineEvents = new ArrayList<>();
}
