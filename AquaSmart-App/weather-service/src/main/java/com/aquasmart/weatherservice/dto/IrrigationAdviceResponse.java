package com.aquasmart.weatherservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IrrigationAdviceResponse implements Serializable {

    private String cityName;
    private Double latitude;
    private Double longitude;
    
    // Recommandation principale
    private Boolean shouldIrrigate;
    private String recommendation;
    private IrrigationUrgency urgency;
    
    // Conditions actuelles
    private Double currentTemperature;
    private Integer currentHumidity;
    private String currentWeather;
    
    // Prévisions pertinentes
    private Double rainExpected24h;     // mm de pluie attendus
    private Double rainProbability;     // Probabilité max
    private Double avgTemperature24h;
    
    // Recommandations détaillées
    private String bestTimeToIrrigate;
    private Double suggestedWaterAmount; // Pourcentage par rapport à la normale
    private String reasoning;
    
    private LocalDateTime generatedAt;

    public enum IrrigationUrgency {
        NONE,       // Pas besoin d'irriguer
        LOW,        // Peut attendre
        MEDIUM,     // À prévoir
        HIGH,       // Urgent
        CRITICAL    // Très urgent
    }
}
