package com.hiresync.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<com.hiresync.notification.entity.JobAppEntity, String> {

    @Query("SELECT j.status, COUNT(j) FROM JobAppEntity j WHERE j.userId = :userId GROUP BY j.status")
    List<Object[]> countByStatusForUser(@Param("userId") String userId);

    @Query("SELECT COUNT(j) FROM JobAppEntity j WHERE j.userId = :userId")
    long countTotalByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(j) FROM JobAppEntity j WHERE j.userId = :userId " +
           "AND j.status IN ('RECRUITER_CALL','TECHNICAL_SCREEN','TAKE_HOME','FINAL_ROUND','OFFER','REJECTED','ACCEPTED')")
    long countRespondedByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(j) FROM JobAppEntity j WHERE j.userId = :userId AND j.status = :status")
    long countByUserIdAndStatus(@Param("userId") String userId, @Param("status") String status);

    @Query("SELECT j.source, COUNT(j) FROM JobAppEntity j WHERE j.userId = :userId AND j.source IS NOT NULL GROUP BY j.source")
    List<Object[]> countBySourceForUser(@Param("userId") String userId);
}
