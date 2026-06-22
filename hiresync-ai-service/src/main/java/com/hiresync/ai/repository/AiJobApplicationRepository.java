package com.hiresync.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface AiJobApplicationRepository extends JpaRepository<com.hiresync.ai.entity.AiJobApplication, String> {

    long countByUserId(String userId);

    @Query("SELECT CAST(j.status AS string) as status, COUNT(j) FROM AiJobApplication j WHERE j.userId = :userId GROUP BY j.status")
    List<Object[]> countByStatusForUser(@Param("userId") String userId);

    @Query(value = """
        SELECT j.id, j.company_name, j.role_title, j.status, j.applied_at,
               j.ai_match_score, j.ats_prediction,
               (NOW() - j.applied_at::timestamp)::text as days_since_applied
        FROM job_applications j WHERE j.user_id = :userId
        ORDER BY j.stage_order ASC
        """, nativeQuery = true)
    List<Map<String, Object>> findSummaryByUserId(@Param("userId") String userId);

    @Query(value = """
        SELECT j.id, j.company_name, j.role_title, j.status, j.job_description,
               j.job_url, j.applied_at, j.ai_match_score, j.ai_feedback, j.ats_prediction,
               j.salary_min, j.salary_max, j.notes
        FROM job_applications j
        WHERE j.user_id = :userId AND lower(j.company_name) LIKE lower(concat('%', :company, '%'))
        LIMIT 1
        """, nativeQuery = true)
    Optional<Map<String, Object>> findDetailByUserIdAndCompany(
        @Param("userId") String userId, @Param("company") String company);
}
