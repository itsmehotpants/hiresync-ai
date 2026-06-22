package com.hiresync.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface AiInterviewRoundRepository extends JpaRepository<com.hiresync.ai.entity.AiInterviewRound, String> {

    @Query(value = """
        SELECT ir.id, ir.round_type, ir.scheduled_at, ir.duration_minutes,
               ja.company_name, ja.role_title
        FROM interview_rounds ir
        JOIN job_applications ja ON ja.id = ir.job_application_id
        WHERE ja.user_id = :userId
        AND ir.scheduled_at BETWEEN :start AND :end
        ORDER BY ir.scheduled_at ASC
        """, nativeQuery = true)
    List<Map<String, Object>> findUpcomingInterviewsForUser(
        @Param("userId") String userId,
        @Param("start") OffsetDateTime start,
        @Param("end") OffsetDateTime end
    );
}
