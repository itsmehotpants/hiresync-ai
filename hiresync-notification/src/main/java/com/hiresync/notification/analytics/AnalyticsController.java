package com.hiresync.notification.analytics;

import com.hiresync.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Job hunt analytics and insights")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/funnel")
    @Operation(summary = "Get application funnel breakdown by status")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getFunnel(
            @RequestParam String userId) {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getFunnel(userId)));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get analytics summary for a user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSummary(
            @RequestParam String userId) {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getSummary(userId)));
    }

    @GetMapping("/by-source")
    @Operation(summary = "Get applications breakdown by source")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getBySource(
            @RequestParam String userId) {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getBySource(userId)));
    }
}
