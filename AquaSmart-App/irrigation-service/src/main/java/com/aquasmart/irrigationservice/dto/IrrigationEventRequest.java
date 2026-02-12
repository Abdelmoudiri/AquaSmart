package com.aquasmart.irrigationservice.dto;

import com.aquasmart.irrigationservice.model.IrrigationSource;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IrrigationEventRequest {
    
    private Long scheduleId;
    
    @NotNull(message = "Parcel ID is required")
    private Long parcelId;
    
    @NotNull(message = "Farm ID is required")
    private Long farmId;
    
    @NotNull(message = "Source is required")
    private IrrigationSource source;
    
    @NotNull(message = "Scheduled start time is required")
    private LocalDateTime scheduledStartTime;
    
    @NotNull(message = "Scheduled end time is required")
    private LocalDateTime scheduledEndTime;
    
    @NotNull(message = "Planned water amount is required")
    @Positive(message = "Planned water amount must be positive")
    private Double plannedWaterAmount;
    
    private Double soilMoistureBefore;
    private Double temperature;
    private Double humidity;
    private Double windSpeed;
    private String notes;
}
