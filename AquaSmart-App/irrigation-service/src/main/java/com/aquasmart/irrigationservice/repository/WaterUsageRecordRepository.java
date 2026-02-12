package com.aquasmart.irrigationservice.repository;

import com.aquasmart.irrigationservice.model.WaterUsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WaterUsageRecordRepository extends JpaRepository<WaterUsageRecord, Long> {
    
    List<WaterUsageRecord> findByFarmId(Long farmId);
    
    List<WaterUsageRecord> findByParcelId(Long parcelId);
    
    @Query("SELECT w FROM WaterUsageRecord w WHERE w.farmId = :farmId " +
           "AND w.recordDate BETWEEN :start AND :end ORDER BY w.recordDate")
    List<WaterUsageRecord> findByFarmIdAndDateRange(
            @Param("farmId") Long farmId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    @Query("SELECT w FROM WaterUsageRecord w WHERE w.parcelId = :parcelId " +
           "AND w.recordDate BETWEEN :start AND :end ORDER BY w.recordDate")
    List<WaterUsageRecord> findByParcelIdAndDateRange(
            @Param("parcelId") Long parcelId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    @Query("SELECT SUM(w.waterUsed) FROM WaterUsageRecord w " +
           "WHERE w.farmId = :farmId AND w.recordDate BETWEEN :start AND :end")
    Double getTotalWaterUsed(
            @Param("farmId") Long farmId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    @Query("SELECT SUM(w.waterSaved) FROM WaterUsageRecord w " +
           "WHERE w.farmId = :farmId AND w.recordDate BETWEEN :start AND :end")
    Double getTotalWaterSaved(
            @Param("farmId") Long farmId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    @Query("SELECT SUM(w.estimatedCost) FROM WaterUsageRecord w " +
           "WHERE w.farmId = :farmId AND w.recordDate BETWEEN :start AND :end")
    Double getTotalCost(
            @Param("farmId") Long farmId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
    
    @Query("SELECT SUM(w.estimatedSavings) FROM WaterUsageRecord w " +
           "WHERE w.farmId = :farmId AND w.recordDate BETWEEN :start AND :end")
    Double getTotalSavings(
            @Param("farmId") Long farmId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
