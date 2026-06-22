package com.hiresync.ai.tools;

import com.hiresync.ai.repository.AiJobApplicationRepository;
import com.hiresync.ai.repository.AiInterviewRoundRepository;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobHuntTools {

    private final AiJobApplicationRepository jobRepo;
    private final AiInterviewRoundRepository interviewRepo;

    @Tool("Get all job applications for the current user with their status, company, role, and AI match scores")
    public List<Map<String, Object>> getAllApplications(@P("userId") String userId) {
        try {
            return jobRepo.findSummaryByUserId(userId);
        } catch (Exception e) {
            log.error("Tool error - getAllApplications: {}", e.getMessage());
            return List.of(Map.of("error", "Could not retrieve applications: " + e.getMessage()));
        }
    }

    @Tool("Get detailed information about a specific job application by company name or partial company name")
    public Map<String, Object> getApplicationByCompany(
            @P("userId") String userId,
            @P("companyName") String companyName) {
        try {
            return jobRepo.findDetailByUserIdAndCompany(userId, companyName)
                    .orElse(Map.of("error", "No application found for company: " + companyName));
        } catch (Exception e) {
            log.error("Tool error - getApplicationByCompany: {}", e.getMessage());
            return Map.of("error", "Could not retrieve application: " + e.getMessage());
        }
    }

    @Tool("Get upcoming interview schedule for the next 7 days for the user")
    public List<Map<String, Object>> getUpcomingInterviews(@P("userId") String userId) {
        try {
            OffsetDateTime now = OffsetDateTime.now();
            OffsetDateTime nextWeek = now.plusDays(7);
            return interviewRepo.findUpcomingInterviewsForUser(userId, now, nextWeek);
        } catch (Exception e) {
            log.error("Tool error - getUpcomingInterviews: {}", e.getMessage());
            return List.of(Map.of("error", "Could not retrieve upcoming interviews: " + e.getMessage()));
        }
    }

    @Tool("Get job application analytics summary including total applications, response rate, and stage breakdown")
    public Map<String, Object> getAnalyticsSummary(@P("userId") String userId) {
        try {
            long total = jobRepo.countByUserId(userId);
            List<Object[]> byStatus = jobRepo.countByStatusForUser(userId);
            Map<String, Long> statusCounts = new java.util.LinkedHashMap<>();
            for (Object[] row : byStatus) {
                statusCounts.put(row[0].toString(), ((Number) row[1]).longValue());
            }
            long responded = statusCounts.getOrDefault("RECRUITER_CALL", 0L)
                    + statusCounts.getOrDefault("TECHNICAL_SCREEN", 0L)
                    + statusCounts.getOrDefault("TAKE_HOME", 0L)
                    + statusCounts.getOrDefault("FINAL_ROUND", 0L)
                    + statusCounts.getOrDefault("OFFER", 0L)
                    + statusCounts.getOrDefault("REJECTED", 0L);
            double responseRate = total > 0 ? (double) responded / total * 100 : 0;

            return Map.of(
                    "totalApplications", total,
                    "responseRate", String.format("%.1f%%", responseRate),
                    "byStatus", statusCounts,
                    "ghosted", statusCounts.getOrDefault("GHOSTED", 0L),
                    "offers", statusCounts.getOrDefault("OFFER", 0L)
            );
        } catch (Exception e) {
            log.error("Tool error - getAnalyticsSummary: {}", e.getMessage());
            return Map.of("error", "Could not retrieve analytics: " + e.getMessage());
        }
    }
}
