package com.aquasmart.irrigationservice.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Water usage statistics response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterUsageStatsResponse {
    
    private Long farmId;
    private Long parcelId;
    
    // Period
    private LocalDate startDate;
    private LocalDate endDate;
    
    // Total usage
    private Double totalWaterUsed;
    private Double totalWaterSaved;
    private Double savingsPercentage;
    
    // Costs
    private Double totalCost;
    private Double totalSavings;
    
    // Averages
    private Double averageDailyUsage;
    private Double averageEfficiencyScore;
    
    // Events count
    private Integer totalEvents;
    private Integer completedEvents;
    private Integer skippedEvents;
    
    // Daily breakdown
    private List<DailyUsage> dailyUsage;
    
    // Usage by source
    private Map<String, Double> usageBySource;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyUsage {
        private LocalDate date;
        private Double waterUsed;
        private Double waterSaved;
        private Integer eventsCount;
    }
}
