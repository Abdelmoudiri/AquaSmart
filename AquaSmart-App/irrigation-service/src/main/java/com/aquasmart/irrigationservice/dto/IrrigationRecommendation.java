package com.aquasmart.irrigationservice.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Smart irrigation recommendation based on weather and sensors
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IrrigationRecommendation {
    
    private Long parcelId;
    private Long farmId;
    
    /**
     * Should irrigate now?
     */
    private Boolean shouldIrrigate;
    
    /**
     * Recommended irrigation duration in minutes
     */
    private Integer recommendedDurationMinutes;
    
    /**
     * Recommended water amount in liters
     */
    private Double recommendedWaterAmount;
    
    /**
     * Optimal time to irrigate
     */
    private LocalDateTime optimalStartTime;
    
    /**
     * Confidence score (0-100)
     */
    private Integer confidenceScore;
    
    /**
     * Current conditions
     */
    private CurrentConditions conditions;
    
    /**
     * Reasoning for the recommendation
     */
    private List<String> reasons;
    
    /**
     * Warnings if any
     */
    private List<String> warnings;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CurrentConditions {
        private Double soilMoisture;
        private Double temperature;
        private Double humidity;
        private Double windSpeed;
        private Double rainProbability;
        private Double expectedRainfall;
        private String weatherDescription;
    }
}
