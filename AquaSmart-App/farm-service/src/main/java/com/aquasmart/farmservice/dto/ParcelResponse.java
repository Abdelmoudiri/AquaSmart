package com.aquasmart.farmservice.dto;

import com.aquasmart.farmservice.model.IrrigationType;
import com.aquasmart.farmservice.model.ParcelStatus;
import com.aquasmart.farmservice.model.SoilType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParcelResponse {

    private Long id;
    private String name;
    private String description;
    private Long farmId;
    private String farmName;
    private Double area;
    private SoilType soilType;
    private ParcelStatus status;
    private IrrigationType irrigationType;
    private Double optimalMoistureMin;
    private Double optimalMoistureMax;
    private Double currentMoisture;
    private LocalDateTime lastIrrigationDate;
    private Double lastIrrigationAmount;
    private List<CropResponse> crops;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
