package com.aquasmart.irrigationservice.dto;

import com.aquasmart.irrigationservice.model.IrrigationPriority;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IrrigationScheduleRequest {
    
    @NotNull(message = "Parcel ID is required")
    private Long parcelId;
    
    @NotNull(message = "Farm ID is required")
    private Long farmId;
    
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;
    
    @NotBlank(message = "Irrigation type is required")
    private String irrigationType;
    
    @NotNull(message = "Start time is required")
    private LocalTime startTime;
    
    @NotNull(message = "Duration is required")
    @Min(value = 1, message = "Duration must be at least 1 minute")
    @Max(value = 480, message = "Duration cannot exceed 8 hours")
    private Integer durationMinutes;
    
    @NotNull(message = "Water amount is required")
    @Positive(message = "Water amount must be positive")
    private Double waterAmountPerSquareMeter;
    
    // Format: "1,2,3,4,5" for Monday-Friday, "0" for Sunday, "6" for Saturday
    private String activeDays;
    
    @Builder.Default
    private Boolean active = true;
    
    @Builder.Default
    private Boolean weatherAware = true;
    
    @Builder.Default
    private Boolean sensorAware = true;
    
    @Min(value = 0, message = "Minimum soil moisture must be at least 0")
    @Max(value = 100, message = "Minimum soil moisture cannot exceed 100")
    private Double minSoilMoistureThreshold;
    
    @Min(value = 0, message = "Maximum soil moisture must be at least 0")
    @Max(value = 100, message = "Maximum soil moisture cannot exceed 100")
    private Double maxSoilMoistureThreshold;
    
    @Builder.Default
    private IrrigationPriority priority = IrrigationPriority.NORMAL;
}
