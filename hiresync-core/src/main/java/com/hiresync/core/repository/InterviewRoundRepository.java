package com.hiresync.core.repository;

import com.hiresync.core.entity.InterviewRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterviewRoundRepository extends JpaRepository<InterviewRound, String> {
    List<InterviewRound> findByJobApplicationIdOrderByRoundNumberAsc(String jobApplicationId);
    Optional<InterviewRound> findByIdAndJobApplicationId(String id, String jobApplicationId);

    @Query("SELECT ir FROM InterviewRound ir " +
           "JOIN JobApplication ja ON ja.id = ir.jobApplicationId " +
           "WHERE ja.userId = :userId " +
           "AND ir.scheduledAt BETWEEN :start AND :end " +
           "ORDER BY ir.scheduledAt ASC")
    List<InterviewRound> findUpcomingForUser(
        @Param("userId") String userId,
        @Param("start") OffsetDateTime start,
        @Param("end") OffsetDateTime end
    );
}
