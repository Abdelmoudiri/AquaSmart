package com.aquasmart.farmservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmRequest {

    @NotBlank(message = "Le nom de la ferme est obligatoire")
    private String name;

    private String description;

    @NotBlank(message = "La localisation est obligatoire")
    private String location;

    private Double latitude;

    private Double longitude;

    @Positive(message = "La surface totale doit être positive")
    private Double totalArea; // en hectares

    private String waterSource;

    private String climateZone;
}
