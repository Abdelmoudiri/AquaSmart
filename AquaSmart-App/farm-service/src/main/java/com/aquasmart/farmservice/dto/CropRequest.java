package com.aquasmart.farmservice.dto;

import com.aquasmart.farmservice.model.CropType;
import com.aquasmart.farmservice.model.GrowthStage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CropRequest {

    @NotBlank(message = "Le nom de la culture est obligatoire")
    private String name;

    private String variety;

    @NotNull(message = "Le type de culture est obligatoire")
    private CropType cropType;

    private GrowthStage growthStage;

    private LocalDate plantingDate;

    private LocalDate expectedHarvestDate;

    @Positive(message = "Le besoin en eau doit être positif")
    private Double waterRequirement; // mm/jour

    private Double optimalTempMin;

    private Double optimalTempMax;

    private String notes;
}
