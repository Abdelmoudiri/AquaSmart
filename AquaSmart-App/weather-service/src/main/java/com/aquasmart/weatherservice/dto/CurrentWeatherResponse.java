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
public class CurrentWeatherResponse implements Serializable {

    private String cityName;
    private String country;
    private Double latitude;
    private Double longitude;
    
    // Température
    private Double temperature;         // °C
    private Double feelsLike;           // °C ressenti
    private Double tempMin;             // °C min
    private Double tempMax;             // °C max
    
    // Humidité et pression
    private Integer humidity;           // %
    private Integer pressure;           // hPa
    
    // Vent
    private Double windSpeed;           // m/s
    private Integer windDirection;      // degrés
    
    // Conditions
    private String weatherMain;         // Rain, Clear, Clouds, etc.
    private String weatherDescription;  // Description détaillée
    private String weatherIcon;         // Code icône
    
    // Nuages et visibilité
    private Integer clouds;             // % couverture nuageuse
    private Integer visibility;         // mètres
    
    // Pluie (optionnel)
    private Double rain1h;              // mm dernière heure
    private Double rain3h;              // mm 3 dernières heures
    
    // Lever/coucher soleil
    private LocalDateTime sunrise;
    private LocalDateTime sunset;
    
    // Métadonnées
    private LocalDateTime timestamp;
    private LocalDateTime fetchedAt;
}
