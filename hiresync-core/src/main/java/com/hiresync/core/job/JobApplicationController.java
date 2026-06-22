package com.hiresync.core.job;

import com.hiresync.common.dto.ApiResponse;
import com.hiresync.core.auth.UserPrincipal;
import com.hiresync.core.job.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Job Applications", description = "Manage job applications")
@SecurityRequirement(name = "bearerAuth")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;

    @PostMapping
    @Operation(summary = "Create a new job application")
    public ResponseEntity<ApiResponse<JobApplicationDTO>> create(
            @Valid @RequestBody CreateJobApplicationRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(jobApplicationService.createApplication(request, principal.getId())));
    }

    @GetMapping("/kanban")
    @Operation(summary = "Get Kanban board grouped by status")
    public ResponseEntity<ApiResponse<Map<String, List<JobApplicationDTO>>>> getKanban(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(jobApplicationService.getKanbanBoard(principal.getId())));
    }

    @GetMapping
    @Operation(summary = "Get all job applications as flat list")
    public ResponseEntity<ApiResponse<List<JobApplicationDTO>>> getAll(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(jobApplicationService.getAllApplications(principal.getId())));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific job application")
    public ResponseEntity<ApiResponse<JobApplicationDTO>> getOne(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(jobApplicationService.getApplication(id, principal.getId())));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a job application")
    public ResponseEntity<ApiResponse<JobApplicationDTO>> update(
            @PathVariable String id,
            @RequestBody UpdateJobApplicationRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(
                jobApplicationService.updateApplication(id, request, principal.getId())));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a job application")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal) {
        jobApplicationService.deleteApplication(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Application deleted", null));
    }

    @PutMapping("/reorder")
    @Operation(summary = "Bulk reorder applications (drag and drop)")
    public ResponseEntity<ApiResponse<Void>> reorder(
            @RequestBody List<ReorderRequest> reorders,
            @AuthenticationPrincipal UserPrincipal principal) {
        jobApplicationService.reorderApplications(principal.getId(), reorders);
        return ResponseEntity.ok(ApiResponse.ok("Reordered", null));
    }

    @PostMapping("/{id}/request-ai-review")
    @Operation(summary = "Request AI analysis for a job application")
    public ResponseEntity<ApiResponse<Void>> requestAiReview(
            @PathVariable String id,
            @RequestParam String resumeId,
            @AuthenticationPrincipal UserPrincipal principal) {
        jobApplicationService.requestAiReview(id, resumeId, principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("AI review requested", null));
    }
}
