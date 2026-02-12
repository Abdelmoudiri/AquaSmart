package com.aquasmart.irrigationservice.repository;

import com.aquasmart.irrigationservice.model.IrrigationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface IrrigationScheduleRepository extends JpaRepository<IrrigationSchedule, Long> {
    
    List<IrrigationSchedule> findByFarmId(Long farmId);
    
    List<IrrigationSchedule> findByParcelId(Long parcelId);
    
    List<IrrigationSchedule> findByFarmIdAndParcelId(Long farmId, Long parcelId);
    
    List<IrrigationSchedule> findByActiveTrue();
    
    List<IrrigationSchedule> findByFarmIdAndActiveTrue(Long farmId);
    
    List<IrrigationSchedule> findByParcelIdAndActiveTrue(Long parcelId);
    
    @Query("SELECT s FROM IrrigationSchedule s WHERE s.active = true " +
           "AND s.startTime BETWEEN :startTime AND :endTime")
    List<IrrigationSchedule> findActiveSchedulesInTimeRange(
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
    
    @Query("SELECT s FROM IrrigationSchedule s WHERE s.active = true " +
           "AND s.weatherAware = true")
    List<IrrigationSchedule> findWeatherAwareSchedules();
    
    @Query("SELECT s FROM IrrigationSchedule s WHERE s.active = true " +
           "AND s.sensorAware = true")
    List<IrrigationSchedule> findSensorAwareSchedules();
    
    @Query("SELECT COUNT(s) FROM IrrigationSchedule s WHERE s.farmId = :farmId")
    long countByFarmId(@Param("farmId") Long farmId);
    
    @Query("SELECT COUNT(s) FROM IrrigationSchedule s WHERE s.parcelId = :parcelId")
    long countByParcelId(@Param("parcelId") Long parcelId);
    
    boolean existsByParcelId(Long parcelId);
    
    void deleteByParcelId(Long parcelId);
}
