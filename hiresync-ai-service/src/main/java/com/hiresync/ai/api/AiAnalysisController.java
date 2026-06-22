package com.hiresync.ai.api;

import com.hiresync.ai.auth.AiJwtService;
import com.hiresync.ai.service.CoverLetterService;
import com.hiresync.ai.service.InterviewPrepService;
import com.hiresync.ai.service.ResumeAnalysisService;
import com.hiresync.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "AI Analysis", description = "Resume analysis, cover letter, interview prep")
public class AiAnalysisController {

    private final ResumeAnalysisService resumeAnalysisService;
    private final CoverLetterService coverLetterService;
    private final InterviewPrepService interviewPrepService;
    private final AiJwtService jwtService;

    @PostMapping("/analyze-resume")
    @Operation(summary = "Analyze resume against a job description")
    public ResponseEntity<ApiResponse<String>> analyzeResume(
            @RequestBody AnalyzeResumeRequest request,
            HttpServletRequest httpRequest) {
        String userId = jwtService.extractUserIdFromRequest(httpRequest);
        log.info("Resume analysis requested by user {}", userId);
        String result = resumeAnalysisService.analyzeResume(request.resumeText(), request.jobDescription());
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping("/cover-letter")
    @Operation(summary = "Generate a cover letter")
    public ResponseEntity<ApiResponse<String>> generateCoverLetter(
            @RequestBody CoverLetterRequest request,
            HttpServletRequest httpRequest) {
        jwtService.extractUserIdFromRequest(httpRequest);
        String result = coverLetterService.generateCoverLetter(
                request.roleTitle(), request.companyName(), request.tone(),
                request.resumeSummary(), request.keyRequirements(), request.motivation()
        );
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @PostMapping("/interview-prep")
    @Operation(summary = "Generate interview questions for a job")
    public ResponseEntity<ApiResponse<String>> interviewPrep(
            @RequestBody InterviewPrepRequest request,
            HttpServletRequest httpRequest) {
        jwtService.extractUserIdFromRequest(httpRequest);
        String result = interviewPrepService.generateInterviewQuestions(
                request.jobDescription(), request.interviewHistory(),
                request.company(), request.role()
        );
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    public record AnalyzeResumeRequest(String resumeText, String jobDescription) {}
    public record CoverLetterRequest(String roleTitle, String companyName, String tone,
                                     String resumeSummary, String keyRequirements, String motivation) {}
    public record InterviewPrepRequest(String jobDescription, String interviewHistory,
                                       String company, String role) {}
}
