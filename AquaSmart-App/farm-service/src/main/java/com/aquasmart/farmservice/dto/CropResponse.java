package com.aquasmart.farmservice.dto;

import com.aquasmart.farmservice.model.CropType;
import com.aquasmart.farmservice.model.GrowthStage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropResponse {

    private Long id;
    private String name;
    private String variety;
    private Long parcelId;
    private String parcelName;
    private CropType cropType;
    private GrowthStage growthStage;
    private LocalDate plantingDate;
    private LocalDate expectedHarvestDate;
    private LocalDate actualHarvestDate;
    private Double waterRequirement;
    private Double optimalTempMin;
    private Double optimalTempMax;
    private String notes;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
