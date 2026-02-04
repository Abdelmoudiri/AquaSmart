package com.aquasmart.weatherservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyForecastResponse implements Serializable {

    private String cityName;
    private String country;
    private Double latitude;
    private Double longitude;
    
    private List<DailyItem> dailyForecasts;
    
    private LocalDateTime fetchedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DailyItem implements Serializable {
        private LocalDate date;
        
        // Températures du jour
        private Double tempMorning;
        private Double tempDay;
        private Double tempEvening;
        private Double tempNight;
        private Double tempMin;
        private Double tempMax;
        
        // Humidité moyenne
        private Integer humidity;
        
        // Vent
        private Double windSpeed;
        
        // Conditions principales
        private String weatherMain;
        private String weatherDescription;
        private String weatherIcon;
        
        // Précipitations
        private Double pop;             // Probabilité
        private Double rainVolume;      // mm total
        
        // Recommandation irrigation
        private Boolean irrigationRecommended;
        private String irrigationNote;
    }
}
