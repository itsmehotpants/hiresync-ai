package com.hiresync.core.repository;

import com.hiresync.core.entity.TimelineEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimelineEventRepository extends JpaRepository<TimelineEvent, String> {
    List<TimelineEvent> findByJobApplicationIdOrderByOccurredAtDesc(String jobApplicationId);
}
