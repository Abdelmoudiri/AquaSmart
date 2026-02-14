package com.aquasmart.irrigationservice.service;

import com.aquasmart.irrigationservice.dto.*;

import java.time.LocalDate;

public interface WaterUsageService {
    
    /**
     * Record water usage
     */
    WaterUsageResponse recordWaterUsage(Long farmId, Long parcelId, Long eventId, 
                                         Double waterUsed, Double waterSaved);
    
    /**
     * Get water usage statistics for a farm
     */
    WaterUsageStatsResponse getWaterUsageStats(Long farmId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get water usage statistics for a parcel
     */
    WaterUsageStatsResponse getParcelWaterUsageStats(Long parcelId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Get daily water usage for a farm
     */
    WaterUsageStatsResponse getDailyWaterUsage(Long farmId, LocalDate date);
    
    /**
     * Get monthly water usage summary for a farm
     */
    WaterUsageStatsResponse getMonthlyWaterUsage(Long farmId, int year, int month);
}
