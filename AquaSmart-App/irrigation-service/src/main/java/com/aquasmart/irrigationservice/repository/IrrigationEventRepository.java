package com.aquasmart.irrigationservice.repository;

import com.aquasmart.irrigationservice.model.IrrigationEvent;
import com.aquasmart.irrigationservice.model.IrrigationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IrrigationEventRepository extends JpaRepository<IrrigationEvent, Long> {
    
    List<IrrigationEvent> findByFarmId(Long farmId);
    
    List<IrrigationEvent> findByParcelId(Long parcelId);
    
    Page<IrrigationEvent> findByFarmId(Long farmId, Pageable pageable);
    
    Page<IrrigationEvent> findByParcelId(Long parcelId, Pageable pageable);
    
    List<IrrigationEvent> findByScheduleId(Long scheduleId);
    
    List<IrrigationEvent> findByStatus(IrrigationStatus status);
    
    @Query("SELECT e FROM IrrigationEvent e WHERE e.status = :status " +
           "AND e.scheduledStartTime BETWEEN :start AND :end")
    List<IrrigationEvent> findByStatusAndTimeRange(
            @Param("status") IrrigationStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    @Query("SELECT e FROM IrrigationEvent e WHERE e.farmId = :farmId " +
           "AND e.scheduledStartTime BETWEEN :start AND :end")
    List<IrrigationEvent> findByFarmIdAndTimeRange(
            @Param("farmId") Long farmId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    @Query("SELECT e FROM IrrigationEvent e WHERE e.parcelId = :parcelId " +
           "AND e.scheduledStartTime BETWEEN :start AND :end")
    List<IrrigationEvent> findByParcelIdAndTimeRange(
            @Param("parcelId") Long parcelId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    @Query("SELECT e FROM IrrigationEvent e WHERE e.status = 'SCHEDULED' " +
           "AND e.scheduledStartTime <= :time ORDER BY e.scheduledStartTime")
    List<IrrigationEvent> findPendingEvents(@Param("time") LocalDateTime time);
    
    @Query("SELECT e FROM IrrigationEvent e WHERE e.status = 'IN_PROGRESS'")
    List<IrrigationEvent> findInProgressEvents();
    
    @Query("SELECT SUM(e.actualWaterAmount) FROM IrrigationEvent e " +
           "WHERE e.farmId = :farmId AND e.status = 'COMPLETED' " +
           "AND e.createdAt BETWEEN :start AND :end")
    Double getTotalWaterUsedByFarm(
            @Param("farmId") Long farmId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    @Query("SELECT SUM(e.waterSaved) FROM IrrigationEvent e " +
           "WHERE e.farmId = :farmId AND e.status IN ('COMPLETED', 'SKIPPED') " +
           "AND e.createdAt BETWEEN :start AND :end")
    Double getTotalWaterSavedByFarm(
            @Param("farmId") Long farmId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    @Query("SELECT AVG(e.efficiencyScore) FROM IrrigationEvent e " +
           "WHERE e.farmId = :farmId AND e.efficiencyScore IS NOT NULL " +
           "AND e.createdAt BETWEEN :start AND :end")
    Double getAverageEfficiencyByFarm(
            @Param("farmId") Long farmId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(e) FROM IrrigationEvent e WHERE e.farmId = :farmId " +
           "AND e.status = :status AND e.createdAt BETWEEN :start AND :end")
    long countByFarmIdAndStatusAndTimeRange(
            @Param("farmId") Long farmId,
            @Param("status") IrrigationStatus status,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    List<IrrigationEvent> findTop10ByParcelIdOrderByCreatedAtDesc(Long parcelId);
    
    List<IrrigationEvent> findTop10ByScheduleIdOrderByCreatedAtDesc(Long scheduleId);
}
