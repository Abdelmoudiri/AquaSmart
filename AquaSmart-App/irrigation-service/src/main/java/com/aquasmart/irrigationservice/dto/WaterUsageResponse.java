package com.aquasmart.irrigationservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterUsageResponse {
    
    private Long id;
    private Long farmId;
    private Long parcelId;
    private Long eventId;
    private LocalDateTime recordDate;
    private Double waterUsed;
    private Double waterSaved;
    private Double estimatedCost;
    private Double estimatedSavings;
    private String source;
}
