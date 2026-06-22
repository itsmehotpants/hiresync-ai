package com.hiresync.core.resume;

import com.hiresync.common.dto.ApiResponse;
import com.hiresync.core.auth.UserPrincipal;
import com.hiresync.core.resume.dto.ResumeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
@Tag(name = "Resumes", description = "Resume upload and management")
@SecurityRequirement(name = "bearerAuth")
public class ResumeController {

    private final ResumeService resumeService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a resume (PDF or DOCX, max 5MB)")
    public ResponseEntity<ApiResponse<ResumeDTO>> upload(
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "label", required = false) String label,
            @AuthenticationPrincipal UserPrincipal principal) throws IOException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(resumeService.uploadResume(file, label, principal.getId())));
    }

    @GetMapping
    @Operation(summary = "List all resumes for current user")
    public ResponseEntity<ApiResponse<List<ResumeDTO>>> getAll(
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(resumeService.getResumes(principal.getId())));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a resume")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal) {
        resumeService.deleteResume(id, principal.getId());
        return ResponseEntity.ok(ApiResponse.ok("Resume deleted", null));
    }

    @PostMapping("/{id}/set-primary")
    @Operation(summary = "Mark resume as primary")
    public ResponseEntity<ApiResponse<ResumeDTO>> setPrimary(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(ApiResponse.ok(resumeService.setPrimary(id, principal.getId())));
    }
}
