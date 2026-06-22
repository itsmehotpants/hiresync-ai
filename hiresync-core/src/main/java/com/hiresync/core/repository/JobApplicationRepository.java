package com.hiresync.core.repository;

import com.hiresync.core.entity.JobApplication;
import com.hiresync.core.entity.JobApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, String> {

    List<JobApplication> findByUserIdOrderByStageOrderAsc(String userId);

    Optional<JobApplication> findByIdAndUserId(String id, String userId);

    long countByUserId(String userId);

    long countByUserIdAndStatus(String userId, JobApplicationStatus status);

    @Query("SELECT j FROM JobApplication j WHERE j.userId = :userId " +
           "AND j.status = 'APPLIED' AND j.appliedAt < :cutoffDate")
    List<JobApplication> findStalledApplications(
        @Param("userId") String userId,
        @Param("cutoffDate") LocalDate cutoffDate
    );

    @Query("SELECT j FROM JobApplication j WHERE j.status = 'APPLIED' " +
           "AND j.appliedAt < :cutoffDate")
    List<JobApplication> findAllStalledApplications(@Param("cutoffDate") LocalDate cutoffDate);

    @Query("SELECT j.status, COUNT(j) FROM JobApplication j WHERE j.userId = :userId GROUP BY j.status")
    List<Object[]> countByStatusForUser(@Param("userId") String userId);

    @Query("SELECT j.source, COUNT(j) FROM JobApplication j WHERE j.userId = :userId AND j.source IS NOT NULL GROUP BY j.source")
    List<Object[]> countBySourceForUser(@Param("userId") String userId);
}
