package com.hiresync.core.job;

import com.hiresync.common.events.AiReviewRequestedEvent;
import com.hiresync.common.events.JobApplicationCreatedEvent;
import com.hiresync.common.events.JobStatusChangedEvent;
import com.hiresync.common.events.NotificationSendEvent;
import com.hiresync.common.exception.HireSyncException;
import com.hiresync.core.entity.JobApplication;
import com.hiresync.core.entity.JobApplicationStatus;
import com.hiresync.core.entity.TimelineEvent;
import com.hiresync.core.job.dto.CreateJobApplicationRequest;
import com.hiresync.core.job.dto.JobApplicationDTO;
import com.hiresync.core.job.dto.ReorderRequest;
import com.hiresync.core.job.dto.UpdateJobApplicationRequest;
import com.hiresync.core.kafka.KafkaEventPublisher;
import com.hiresync.core.repository.JobApplicationRepository;
import com.hiresync.core.repository.TimelineEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final TimelineEventRepository timelineEventRepository;
    private final JobApplicationMapper mapper;
    private final KafkaEventPublisher eventPublisher;

    @CacheEvict(value = "kanban", key = "#userId")
    public JobApplicationDTO createApplication(CreateJobApplicationRequest request, String userId) {
        JobApplication app = new JobApplication();
        app.setUserId(userId);
        app.setCompanyName(request.companyName());
        app.setRoleTitle(request.roleTitle());
        app.setJobUrl(request.jobUrl());
        app.setJobDescription(request.jobDescription());
        app.setStatus(request.appliedAt() != null
                ? JobApplicationStatus.APPLIED
                : JobApplicationStatus.WISHLIST);
        app.setSource(request.source());
        app.setLocation(request.location());
        app.setRemote(request.isRemote());
        app.setSalaryMin(request.salaryMin());
        app.setSalaryMax(request.salaryMax());
        app.setSalaryCurrency(request.salaryCurrency() != null ? request.salaryCurrency() : "INR");
        app.setNotes(request.notes());
        app.setAppliedAt(request.appliedAt());
        app.setDeadlineAt(request.deadlineAt());
        app = jobApplicationRepository.save(app);

        createTimelineEvent(app.getId(), "APPLICATION_CREATED",
                "Application created for " + app.getCompanyName());

        eventPublisher.publishJobCreated(JobApplicationCreatedEvent.builder()
                .userId(userId)
                .jobApplicationId(app.getId())
                .companyName(app.getCompanyName())
                .roleTitle(app.getRoleTitle())
                .status(app.getStatus().name())
                .build());

        log.info("Created job application {} for user {}", app.getId(), userId);
        return mapper.toDTO(app);
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "kanban", key = "#userId")
    public Map<String, List<JobApplicationDTO>> getKanbanBoard(String userId) {
        List<JobApplication> apps = jobApplicationRepository.findByUserIdOrderByStageOrderAsc(userId);
        return apps.stream()
                .map(mapper::toDTO)
                .collect(Collectors.groupingBy(dto -> dto.status().name()));
    }

    @Transactional(readOnly = true)
    public List<JobApplicationDTO> getAllApplications(String userId) {
        return mapper.toDTOList(
                jobApplicationRepository.findByUserIdOrderByStageOrderAsc(userId));
    }

    @Transactional(readOnly = true)
    public JobApplicationDTO getApplication(String jobId, String userId) {
        return mapper.toDTO(findOwnedApplication(jobId, userId));
    }

    @CacheEvict(value = "kanban", key = "#userId")
    public JobApplicationDTO updateApplication(String jobId, UpdateJobApplicationRequest request, String userId) {
        JobApplication app = findOwnedApplication(jobId, userId);
        JobApplicationStatus oldStatus = app.getStatus();

        if (request.companyName() != null) app.setCompanyName(request.companyName());
        if (request.roleTitle() != null) app.setRoleTitle(request.roleTitle());
        if (request.jobUrl() != null) app.setJobUrl(request.jobUrl());
        if (request.jobDescription() != null) app.setJobDescription(request.jobDescription());
        if (request.source() != null) app.setSource(request.source());
        if (request.location() != null) app.setLocation(request.location());
        if (request.isRemote() != null) app.setRemote(request.isRemote());
        if (request.salaryMin() != null) app.setSalaryMin(request.salaryMin());
        if (request.salaryMax() != null) app.setSalaryMax(request.salaryMax());
        if (request.salaryCurrency() != null) app.setSalaryCurrency(request.salaryCurrency());
        if (request.notes() != null) app.setNotes(request.notes());
        if (request.appliedAt() != null) app.setAppliedAt(request.appliedAt());
        if (request.deadlineAt() != null) app.setDeadlineAt(request.deadlineAt());

        if (request.status() != null && request.status() != oldStatus) {
            app.setStatus(request.status());
            createTimelineEvent(app.getId(), "STATUS_CHANGED",
                    "Status changed from " + oldStatus.name() + " to " + request.status().name());

            eventPublisher.publishStatusChanged(JobStatusChangedEvent.builder()
                    .userId(userId)
                    .jobApplicationId(app.getId())
                    .companyName(app.getCompanyName())
                    .roleTitle(app.getRoleTitle())
                    .oldStatus(oldStatus.name())
                    .newStatus(request.status().name())
                    .build());

            if (request.status() == JobApplicationStatus.OFFER) {
                eventPublisher.publishNotification(NotificationSendEvent.builder()
                        .userId(userId)
                        .notificationType("OFFER_RECEIVED")
                        .title("🎉 Offer Received!")
                        .body("Congratulations! You received an offer from " + app.getCompanyName())
                        .relatedEntityId(app.getId())
                        .sendEmail(true)
                        .build());
            }
        }

        return mapper.toDTO(jobApplicationRepository.save(app));
    }

    @CacheEvict(value = "kanban", key = "#userId")
    public void deleteApplication(String jobId, String userId) {
        JobApplication app = findOwnedApplication(jobId, userId);
        jobApplicationRepository.delete(app);
        log.info("Deleted job application {} for user {}", jobId, userId);
    }

    @CacheEvict(value = "kanban", key = "#userId")
    public void reorderApplications(String userId, List<ReorderRequest> reorders) {
        reorders.forEach(r -> {
            jobApplicationRepository.findByIdAndUserId(r.jobId(), userId).ifPresent(app -> {
                app.setStageOrder(r.newOrder());
                jobApplicationRepository.save(app);
            });
        });
    }

    public void requestAiReview(String jobId, String resumeId, String userId) {
        JobApplication app = findOwnedApplication(jobId, userId);
        eventPublisher.publishAiReviewRequested(AiReviewRequestedEvent.builder()
                .userId(userId)
                .jobApplicationId(app.getId())
                .resumeId(resumeId)
                .build());
    }

    // Called by AI service consumer to update scores
    @CacheEvict(value = "kanban", key = "#userId")
    public void updateAiScore(String jobId, String userId, int score, String feedback, String atsPrediction) {
        jobApplicationRepository.findByIdAndUserId(jobId, userId).ifPresent(app -> {
            app.setAiMatchScore(score);
            app.setAiFeedback(feedback);
            app.setAtsPrediction(atsPrediction);
            jobApplicationRepository.save(app);
        });
    }

    private JobApplication findOwnedApplication(String jobId, String userId) {
        // Always return 403 not 404 to prevent enumeration attacks
        JobApplication app = jobApplicationRepository.findById(jobId)
                .orElseThrow(() -> HireSyncException.forbidden());
        if (!app.getUserId().equals(userId)) {
            throw HireSyncException.forbidden();
        }
        return app;
    }

    private void createTimelineEvent(String jobAppId, String eventType, String description) {
        TimelineEvent event = new TimelineEvent();
        event.setJobApplicationId(jobAppId);
        event.setEventType(eventType);
        event.setDescription(description);
        event.setOccurredAt(OffsetDateTime.now());
        timelineEventRepository.save(event);
    }
}
