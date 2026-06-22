package com.hiresync.notification.analytics;

import com.hiresync.notification.repository.AnalyticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public List<Map<String, Object>> getFunnel(String userId) {
        List<Object[]> rows = analyticsRepository.countByStatusForUser(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Integer> order = Map.of(
                "WISHLIST", 0, "APPLIED", 1, "RECRUITER_CALL", 2,
                "TECHNICAL_SCREEN", 3, "TAKE_HOME", 4, "FINAL_ROUND", 5,
                "OFFER", 6, "NEGOTIATING", 7, "ACCEPTED", 8,
                "REJECTED", 9, "WITHDRAWN", 10, "GHOSTED", 11
        );
        for (Object[] row : rows) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("status", row[0]);
            item.put("count", row[1]);
            item.put("order", order.getOrDefault(row[0].toString(), 99));
            result.add(item);
        }
        result.sort(Comparator.comparingInt(m -> (Integer) m.get("order")));
        return result;
    }

    public Map<String, Object> getSummary(String userId) {
        long total = analyticsRepository.countTotalByUserId(userId);
        long responded = analyticsRepository.countRespondedByUserId(userId);
        long ghosted = analyticsRepository.countByUserIdAndStatus(userId, "GHOSTED");
        long offers = analyticsRepository.countByUserIdAndStatus(userId, "OFFER");
        double responseRate = total > 0 ? (double) responded / total * 100 : 0;

        return Map.of(
                "total", total,
                "responded", responded,
                "ghosted", ghosted,
                "offers", offers,
                "responseRate", String.format("%.1f", responseRate)
        );
    }

    public List<Map<String, Object>> getBySource(String userId) {
        List<Object[]> rows = analyticsRepository.countBySourceForUser(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> item = new HashMap<>();
            item.put("source", row[0]);
            item.put("count", row[1]);
            result.add(item);
        }
        return result;
    }
}
