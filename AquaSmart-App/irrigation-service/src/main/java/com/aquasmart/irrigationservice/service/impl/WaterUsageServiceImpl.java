package com.aquasmart.irrigationservice.service.impl;

import com.aquasmart.irrigationservice.dto.*;
import com.aquasmart.irrigationservice.model.*;
import com.aquasmart.irrigationservice.repository.WaterUsageRecordRepository;
import com.aquasmart.irrigationservice.service.WaterUsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WaterUsageServiceImpl implements WaterUsageService {
    
    private final WaterUsageRecordRepository usageRepository;
    
    @Value("${irrigation.water-cost-per-liter:0.005}")
    private Double waterCostPerLiter; // Cost in MAD per liter
    
    @Override
    public WaterUsageResponse recordWaterUsage(Long farmId, Long parcelId, Long eventId, 
                                                 Double waterUsed, Double waterSaved) {
        log.info("Recording water usage - Farm: {}, Parcel: {}, Used: {}L, Saved: {}L", 
                 farmId, parcelId, waterUsed, waterSaved);
        
        WaterUsageRecord record = WaterUsageRecord.builder()
                .farmId(farmId)
                .parcelId(parcelId)
                .eventId(eventId)
                .recordDate(LocalDateTime.now())
                .waterUsed(waterUsed != null ? waterUsed : 0.0)
                .waterSaved(waterSaved != null ? waterSaved : 0.0)
                .estimatedCost(waterUsed != null ? waterUsed * waterCostPerLiter : 0.0)
                .estimatedSavings(waterSaved != null ? waterSaved * waterCostPerLiter : 0.0)
                .source(IrrigationSource.SCHEDULED)
                .build();
        
        record = usageRepository.save(record);
        
        return mapToResponse(record);
    }
    
    @Override
    @Transactional(readOnly = true)
    public WaterUsageStatsResponse getWaterUsageStats(Long farmId, LocalDate startDate, LocalDate endDate) {
        log.info("Getting water usage stats for farm: {} from {} to {}", farmId, startDate, endDate);
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        
        List<WaterUsageRecord> records = usageRepository.findByFarmIdAndDateRange(farmId, start, end);
        
        return buildStatsResponse(farmId, null, startDate, endDate, records);
    }
    
    @Override
    @Transactional(readOnly = true)
    public WaterUsageStatsResponse getParcelWaterUsageStats(Long parcelId, LocalDate startDate, LocalDate endDate) {
        log.info("Getting water usage stats for parcel: {} from {} to {}", parcelId, startDate, endDate);
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        
        List<WaterUsageRecord> records = usageRepository.findByParcelIdAndDateRange(parcelId, start, end);
        
        return buildStatsResponse(null, parcelId, startDate, endDate, records);
    }
    
    @Override
    @Transactional(readOnly = true)
    public WaterUsageStatsResponse getDailyWaterUsage(Long farmId, LocalDate date) {
        return getWaterUsageStats(farmId, date, date);
    }
    
    @Override
    @Transactional(readOnly = true)
    public WaterUsageStatsResponse getMonthlyWaterUsage(Long farmId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();
        
        return getWaterUsageStats(farmId, startDate, endDate);
    }
    
    private WaterUsageStatsResponse buildStatsResponse(Long farmId, Long parcelId,
                                                        LocalDate startDate, LocalDate endDate,
                                                        List<WaterUsageRecord> records) {
        double totalWaterUsed = records.stream()
                .mapToDouble(r -> r.getWaterUsed() != null ? r.getWaterUsed() : 0)
                .sum();
        
        double totalWaterSaved = records.stream()
                .mapToDouble(r -> r.getWaterSaved() != null ? r.getWaterSaved() : 0)
                .sum();
        
        double totalCost = records.stream()
                .mapToDouble(r -> r.getEstimatedCost() != null ? r.getEstimatedCost() : 0)
                .sum();
        
        double totalSavings = records.stream()
                .mapToDouble(r -> r.getEstimatedSavings() != null ? r.getEstimatedSavings() : 0)
                .sum();
        
        // Calculate savings percentage
        double savingsPercentage = 0;
        if (totalWaterUsed + totalWaterSaved > 0) {
            savingsPercentage = (totalWaterSaved / (totalWaterUsed + totalWaterSaved)) * 100;
        }
        
        // Calculate daily breakdown
        Map<LocalDate, List<WaterUsageRecord>> recordsByDate = records.stream()
                .collect(Collectors.groupingBy(r -> r.getRecordDate().toLocalDate()));
        
        List<WaterUsageStatsResponse.DailyUsage> dailyUsage = new ArrayList<>();
        for (Map.Entry<LocalDate, List<WaterUsageRecord>> entry : recordsByDate.entrySet()) {
            double dayUsed = entry.getValue().stream()
                    .mapToDouble(r -> r.getWaterUsed() != null ? r.getWaterUsed() : 0)
                    .sum();
            double daySaved = entry.getValue().stream()
                    .mapToDouble(r -> r.getWaterSaved() != null ? r.getWaterSaved() : 0)
                    .sum();
            
            dailyUsage.add(WaterUsageStatsResponse.DailyUsage.builder()
                    .date(entry.getKey())
                    .waterUsed(dayUsed)
                    .waterSaved(daySaved)
                    .eventsCount(entry.getValue().size())
                    .build());
        }
        dailyUsage.sort(Comparator.comparing(WaterUsageStatsResponse.DailyUsage::getDate));
        
        // Calculate usage by source
        Map<String, Double> usageBySource = records.stream()
                .filter(r -> r.getSource() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getSource().name(),
                        Collectors.summingDouble(r -> r.getWaterUsed() != null ? r.getWaterUsed() : 0)
                ));
        
        // Calculate days in period
        long daysPeriod = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        double averageDailyUsage = daysPeriod > 0 ? totalWaterUsed / daysPeriod : 0;
        
        return WaterUsageStatsResponse.builder()
                .farmId(farmId)
                .parcelId(parcelId)
                .startDate(startDate)
                .endDate(endDate)
                .totalWaterUsed(totalWaterUsed)
                .totalWaterSaved(totalWaterSaved)
                .savingsPercentage(Math.round(savingsPercentage * 100.0) / 100.0)
                .totalCost(Math.round(totalCost * 100.0) / 100.0)
                .totalSavings(Math.round(totalSavings * 100.0) / 100.0)
                .averageDailyUsage(Math.round(averageDailyUsage * 100.0) / 100.0)
                .averageEfficiencyScore(null) // Would need event data
                .totalEvents(records.size())
                .completedEvents(records.size())
                .skippedEvents(0)
                .dailyUsage(dailyUsage)
                .usageBySource(usageBySource)
                .build();
    }
    
    private WaterUsageResponse mapToResponse(WaterUsageRecord record) {
        return WaterUsageResponse.builder()
                .id(record.getId())
                .farmId(record.getFarmId())
                .parcelId(record.getParcelId())
                .eventId(record.getEventId())
                .recordDate(record.getRecordDate())
                .waterUsed(record.getWaterUsed())
                .waterSaved(record.getWaterSaved())
                .estimatedCost(record.getEstimatedCost())
                .estimatedSavings(record.getEstimatedSavings())
                .source(record.getSource() != null ? record.getSource().name() : null)
                .build();
    }
}
