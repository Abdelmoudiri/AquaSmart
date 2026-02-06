package com.aquasmart.weatherservice.service;

import com.aquasmart.weatherservice.dto.CurrentWeatherResponse;
import com.aquasmart.weatherservice.dto.DailyForecastResponse;
import com.aquasmart.weatherservice.dto.ForecastResponse;
import com.aquasmart.weatherservice.dto.IrrigationAdviceResponse;

public interface WeatherService {

    /**
     * Récupère la météo actuelle par coordonnées GPS
     */
    CurrentWeatherResponse getCurrentWeatherByCoords(Double lat, Double lon);

    /**
     * Récupère la météo actuelle par nom de ville
     */
    CurrentWeatherResponse getCurrentWeatherByCity(String cityName);

    /**
     * Récupère les prévisions horaires (5 jours, intervalles 3h)
     */
    ForecastResponse getForecastByCoords(Double lat, Double lon);

    /**
     * Récupère les prévisions horaires par ville
     */
    ForecastResponse getForecastByCity(String cityName);

    /**
     * Récupère les prévisions journalières agrégées
     */
    DailyForecastResponse getDailyForecastByCoords(Double lat, Double lon, Integer days);

    /**
     * Génère des conseils d'irrigation basés sur la météo
     */
    IrrigationAdviceResponse getIrrigationAdvice(Double lat, Double lon);

    /**
     * Génère des conseils d'irrigation par ville
     */
    IrrigationAdviceResponse getIrrigationAdviceByCity(String cityName);
}
