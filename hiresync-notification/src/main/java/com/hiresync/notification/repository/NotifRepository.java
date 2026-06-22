package com.hiresync.notification.repository;

import com.hiresync.notification.entity.NotifEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotifRepository extends JpaRepository<NotifEntity, String> {
    List<NotifEntity> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<NotifEntity> findByIdAndUserId(String id, String userId);

    @Query("SELECT COUNT(n) FROM NotifEntity n WHERE n.userId = :userId AND n.isRead = false")
    long countUnreadByUserId(@Param("userId") String userId);

    @Modifying
    @Query("UPDATE NotifEntity n SET n.isRead = true WHERE n.userId = :userId")
    void markAllReadForUser(@Param("userId") String userId);
}
