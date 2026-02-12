package com.aquasmart.irrigationservice.dto;

import com.aquasmart.irrigationservice.model.IrrigationSource;
import com.aquasmart.irrigationservice.model.IrrigationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IrrigationEventResponse {
    
    private Long id;
    private Long scheduleId;
    private String scheduleName;
    private Long parcelId;
    private Long farmId;
    private IrrigationSource source;
    private IrrigationStatus status;
    private LocalDateTime scheduledStartTime;
    private LocalDateTime scheduledEndTime;
    private LocalDateTime actualStartTime;
    private LocalDateTime actualEndTime;
    private Double plannedWaterAmount;
    private Double actualWaterAmount;
    private Double soilMoistureBefore;
    private Double soilMoistureAfter;
    private Double temperature;
    private Double humidity;
    private Double windSpeed;
    private String skipReason;
    private String notes;
    private Double waterSaved;
    private Integer efficiencyScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
