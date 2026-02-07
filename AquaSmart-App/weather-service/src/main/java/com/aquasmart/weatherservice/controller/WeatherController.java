package com.aquasmart.weatherservice.controller;

import com.aquasmart.weatherservice.dto.*;
import com.aquasmart.weatherservice.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class WeatherController {

    private final WeatherService weatherService;

    // ========== MÉTÉO ACTUELLE ==========

    @GetMapping("/current")
    public ResponseEntity<CurrentWeatherResponse> getCurrentWeatherByCoords(
            @RequestParam Double lat,
            @RequestParam Double lon) {
        log.info("Request for current weather at coords: {}, {}", lat, lon);
        return ResponseEntity.ok(weatherService.getCurrentWeatherByCoords(lat, lon));
    }

    @GetMapping("/current/city/{cityName}")
    public ResponseEntity<CurrentWeatherResponse> getCurrentWeatherByCity(
            @PathVariable String cityName) {
        log.info("Request for current weather in city: {}", cityName);
        return ResponseEntity.ok(weatherService.getCurrentWeatherByCity(cityName));
    }

    // ========== PRÉVISIONS HORAIRES ==========

    @GetMapping("/forecast")
    public ResponseEntity<ForecastResponse> getForecastByCoords(
            @RequestParam Double lat,
            @RequestParam Double lon) {
        log.info("Request for forecast at coords: {}, {}", lat, lon);
        return ResponseEntity.ok(weatherService.getForecastByCoords(lat, lon));
    }

    @GetMapping("/forecast/city/{cityName}")
    public ResponseEntity<ForecastResponse> getForecastByCity(
            @PathVariable String cityName) {
        log.info("Request for forecast in city: {}", cityName);
        return ResponseEntity.ok(weatherService.getForecastByCity(cityName));
    }

    // ========== PRÉVISIONS JOURNALIÈRES ==========

    @GetMapping("/forecast/daily")
    public ResponseEntity<DailyForecastResponse> getDailyForecast(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(defaultValue = "5") Integer days) {
        log.info("Request for {} days daily forecast at coords: {}, {}", days, lat, lon);
        return ResponseEntity.ok(weatherService.getDailyForecastByCoords(lat, lon, days));
    }

    // ========== CONSEILS IRRIGATION ==========

    @GetMapping("/irrigation-advice")
    public ResponseEntity<IrrigationAdviceResponse> getIrrigationAdvice(
            @RequestParam Double lat,
            @RequestParam Double lon) {
        log.info("Request for irrigation advice at coords: {}, {}", lat, lon);
        return ResponseEntity.ok(weatherService.getIrrigationAdvice(lat, lon));
    }

    @GetMapping("/irrigation-advice/city/{cityName}")
    public ResponseEntity<IrrigationAdviceResponse> getIrrigationAdviceByCity(
            @PathVariable String cityName) {
        log.info("Request for irrigation advice in city: {}", cityName);
        return ResponseEntity.ok(weatherService.getIrrigationAdviceByCity(cityName));
    }

    // ========== HEALTH CHECK ==========

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Weather Service is running!");
    }
}
