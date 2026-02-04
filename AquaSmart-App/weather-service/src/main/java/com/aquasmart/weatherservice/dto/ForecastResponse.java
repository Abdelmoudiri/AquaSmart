package com.aquasmart.weatherservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForecastResponse implements Serializable {

    private String cityName;
    private String country;
    private Double latitude;
    private Double longitude;
    
    private List<ForecastItem> forecasts;
    
    private LocalDateTime fetchedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ForecastItem implements Serializable {
        private LocalDateTime dateTime;
        
        // Température
        private Double temperature;
        private Double feelsLike;
        private Double tempMin;
        private Double tempMax;
        
        // Humidité et pression
        private Integer humidity;
        private Integer pressure;
        
        // Vent
        private Double windSpeed;
        private Integer windDirection;
        
        // Conditions
        private String weatherMain;
        private String weatherDescription;
        private String weatherIcon;
        
        // Nuages
        private Integer clouds;
        
        // Précipitations
        private Double pop;             // Probabilité de précipitations (0-1)
        private Double rain3h;          // mm de pluie sur 3h
        private Double snow3h;          // mm de neige sur 3h
    }
}
