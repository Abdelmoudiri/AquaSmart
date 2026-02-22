package com.aquasmart.alertservice.repository;

import com.aquasmart.alertservice.model.Alert;
import com.aquasmart.alertservice.model.AlertSeverity;
import com.aquasmart.alertservice.model.AlertStatus;
import com.aquasmart.alertservice.model.AlertType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    
    Page<Alert> findByUserId(Long userId, Pageable pageable);
    
    Page<Alert> findByFarmId(Long farmId, Pageable pageable);
    
    Page<Alert> findByParcelId(Long parcelId, Pageable pageable);
    
    List<Alert> findByUserIdAndStatus(Long userId, AlertStatus status);
    
    List<Alert> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, AlertStatus status);
    
    Page<Alert> findByUserIdAndSeverity(Long userId, AlertSeverity severity, Pageable pageable);
    
    Page<Alert> findByUserIdAndType(Long userId, AlertType type, Pageable pageable);
    
    @Query("SELECT a FROM Alert a WHERE a.userId = :userId " +
           "AND a.createdAt BETWEEN :start AND :end " +
           "ORDER BY a.createdAt DESC")
    List<Alert> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    @Query("SELECT a FROM Alert a WHERE a.farmId = :farmId " +
           "AND a.status = 'NEW' ORDER BY a.severity DESC, a.createdAt DESC")
    List<Alert> findNewAlertsByFarmId(@Param("farmId") Long farmId);
    
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.userId = :userId AND a.status = 'NEW'")
    long countNewAlertsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.userId = :userId AND a.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") AlertStatus status);
    
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.userId = :userId AND a.severity = :severity")
    long countByUserIdAndSeverity(@Param("userId") Long userId, @Param("severity") AlertSeverity severity);
    
    @Query("SELECT a.type, COUNT(a) FROM Alert a WHERE a.userId = :userId " +
           "GROUP BY a.type")
    List<Object[]> countByUserIdGroupByType(@Param("userId") Long userId);
    
    @Query("SELECT a FROM Alert a WHERE a.userId = :userId " +
           "AND a.status IN ('NEW', 'READ') " +
           "AND a.severity IN ('CRITICAL', 'EMERGENCY') " +
           "ORDER BY a.severity DESC, a.createdAt DESC")
    List<Alert> findUrgentAlertsByUserId(@Param("userId") Long userId);
    
    List<Alert> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<Alert> findTop10ByFarmIdOrderByCreatedAtDesc(Long farmId);
}
