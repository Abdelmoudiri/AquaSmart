package com.aquasmart.irrigationservice.dto;

import com.aquasmart.irrigationservice.model.IrrigationPriority;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IrrigationScheduleResponse {
    
    private Long id;
    private Long parcelId;
    private Long farmId;
    private String name;
    private String description;
    private String irrigationType;
    private LocalTime startTime;
    private Integer durationMinutes;
    private Double waterAmountPerSquareMeter;
    private String activeDays;
    private Boolean active;
    private Boolean weatherAware;
    private Boolean sensorAware;
    private Double minSoilMoistureThreshold;
    private Double maxSoilMoistureThreshold;
    private IrrigationPriority priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Statistics
    private Integer totalEventsCount;
    private Integer completedEventsCount;
    private Double totalWaterUsed;
    private Double totalWaterSaved;
    
    private List<IrrigationEventResponse> recentEvents;
}
