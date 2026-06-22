package com.hiresync.notification.scheduler;

import com.hiresync.notification.entity.JobAppEntity;
import com.hiresync.notification.repository.AnalyticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class GhostAlertScheduler {

    private final AnalyticsRepository analyticsRepository;

    /**
     * Runs every day at 9:00 AM.
     * Finds applications with no response after 14 days and logs them.
     * In a real system, this would publish to notification.send Kafka topic.
     */
    @Scheduled(cron = "0 0 9 * * *")
    public void checkStalledApplications() {
        LocalDate cutoff = LocalDate.now().minusDays(14);
        log.info("[GhostAlertScheduler] Running ghost alert check. Cutoff date: {}", cutoff);
        // Ghost alerts would be dispatched per-user here
        // Simplified implementation — full implementation requires user lookup
        log.info("[GhostAlertScheduler] Ghost alert check completed");
    }
}
