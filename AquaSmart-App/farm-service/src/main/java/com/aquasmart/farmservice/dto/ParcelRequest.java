package com.aquasmart.farmservice.dto;

import com.aquasmart.farmservice.model.IrrigationType;
import com.aquasmart.farmservice.model.SoilType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParcelRequest {

    @NotBlank(message = "Le nom de la parcelle est obligatoire")
    private String name;

    private String description;

    @NotNull(message = "La superficie est obligatoire")
    @Positive(message = "La superficie doit être positive")
    private Double area; // en hectares

    @NotNull(message = "Le type de sol est obligatoire")
    private SoilType soilType;

    private IrrigationType irrigationType;

    @Positive(message = "L'humidité minimum doit être positive")
    private Double optimalMoistureMin;

    @Positive(message = "L'humidité maximum doit être positive")
    private Double optimalMoistureMax;
}
