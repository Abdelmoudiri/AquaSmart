package com.aquasmart.irrigationservice.controller;

import com.aquasmart.irrigationservice.dto.*;
import com.aquasmart.irrigationservice.service.SmartIrrigationService;
import com.aquasmart.irrigationservice.service.WaterUsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/irrigation")
@RequiredArgsConstructor
@Slf4j
public class SmartIrrigationController {
    
    private final SmartIrrigationService smartIrrigationService;
    private final WaterUsageService waterUsageService;
    
    /**
     * Get smart irrigation recommendation for a parcel
     */
    @GetMapping("/recommendation")
    public ResponseEntity<IrrigationRecommendation> getRecommendation(
            @RequestParam Long parcelId,
            @RequestParam Long farmId,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) Double soilMoisture) {
        log.info("Getting irrigation recommendation for parcel: {}", parcelId);
        
        IrrigationRecommendation recommendation = smartIrrigationService.getRecommendation(
                parcelId, farmId, latitude, longitude, soilMoisture);
        
        return ResponseEntity.ok(recommendation);
    }
    
    /**
     * Check if irrigation should be skipped based on weather
     */
    @GetMapping("/should-skip")
    public ResponseEntity<Boolean> shouldSkipIrrigation(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        boolean shouldSkip = smartIrrigationService.shouldSkipIrrigation(latitude, longitude);
        return ResponseEntity.ok(shouldSkip);
    }
    
    /**
     * Get water usage statistics for a farm
     */
    @GetMapping("/stats/farm/{farmId}")
    public ResponseEntity<WaterUsageStatsResponse> getFarmWaterUsageStats(
            @PathVariable Long farmId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Getting water usage stats for farm: {} from {} to {}", farmId, startDate, endDate);
        
        WaterUsageStatsResponse stats = waterUsageService.getWaterUsageStats(farmId, startDate, endDate);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Get water usage statistics for a parcel
     */
    @GetMapping("/stats/parcel/{parcelId}")
    public ResponseEntity<WaterUsageStatsResponse> getParcelWaterUsageStats(
            @PathVariable Long parcelId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Getting water usage stats for parcel: {} from {} to {}", parcelId, startDate, endDate);
        
        WaterUsageStatsResponse stats = waterUsageService.getParcelWaterUsageStats(parcelId, startDate, endDate);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Get daily water usage for a farm
     */
    @GetMapping("/stats/farm/{farmId}/daily")
    public ResponseEntity<WaterUsageStatsResponse> getDailyWaterUsage(
            @PathVariable Long farmId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Getting daily water usage for farm: {} on {}", farmId, date);
        
        WaterUsageStatsResponse stats = waterUsageService.getDailyWaterUsage(farmId, date);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Get monthly water usage for a farm
     */
    @GetMapping("/stats/farm/{farmId}/monthly")
    public ResponseEntity<WaterUsageStatsResponse> getMonthlyWaterUsage(
            @PathVariable Long farmId,
            @RequestParam int year,
            @RequestParam int month) {
        log.info("Getting monthly water usage for farm: {} - {}/{}", farmId, month, year);
        
        WaterUsageStatsResponse stats = waterUsageService.getMonthlyWaterUsage(farmId, year, month);
        return ResponseEntity.ok(stats);
    }
}
