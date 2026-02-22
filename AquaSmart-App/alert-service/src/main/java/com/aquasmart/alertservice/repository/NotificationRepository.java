package com.aquasmart.alertservice.repository;

import com.aquasmart.alertservice.model.Notification;
import com.aquasmart.alertservice.model.NotificationChannel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByAlertId(Long alertId);
    
    List<Notification> findByUserId(Long userId);
    
    List<Notification> findByUserIdAndChannel(Long userId, NotificationChannel channel);
    
    @Query("SELECT n FROM Notification n WHERE n.sent = false AND n.retryCount < 3")
    List<Notification> findFailedNotifications();
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userId = :userId " +
           "AND n.createdAt BETWEEN :start AND :end")
    long countByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
