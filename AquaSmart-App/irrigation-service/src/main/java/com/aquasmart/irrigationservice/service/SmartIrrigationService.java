package com.aquasmart.irrigationservice.service;

import com.aquasmart.irrigationservice.dto.IrrigationRecommendation;

public interface SmartIrrigationService {
    
    /**
     * Get smart irrigation recommendation for a parcel
     * based on weather data, soil moisture, and crop requirements
     */
    IrrigationRecommendation getRecommendation(Long parcelId, Long farmId, 
                                                Double latitude, Double longitude,
                                                Double currentSoilMoisture);
    
    /**
     * Check if irrigation should be skipped based on weather
     */
    boolean shouldSkipIrrigation(Double latitude, Double longitude);
    
    /**
     * Calculate optimal water amount based on conditions
     */
    Double calculateOptimalWaterAmount(Long parcelId, Double soilMoisture, 
                                        Double temperature, Double humidity);
    
    /**
     * Evaluate irrigation efficiency after completion
     */
    Integer evaluateEfficiency(Double plannedWater, Double actualWater,
                               Double soilMoistureBefore, Double soilMoistureAfter,
                               Double temperature);
}
