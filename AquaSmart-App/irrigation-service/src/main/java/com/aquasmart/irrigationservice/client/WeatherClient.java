package com.aquasmart.irrigationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Feign client for Weather Service
 */
@FeignClient(name = "weather-service", fallback = WeatherClientFallback.class)
public interface WeatherClient {
    
    @GetMapping("/api/weather/current")
    Map<String, Object> getCurrentWeather(
            @RequestParam("lat") Double latitude,
            @RequestParam("lon") Double longitude);
    
    @GetMapping("/api/weather/forecast")
    Map<String, Object> getForecast(
            @RequestParam("lat") Double latitude,
            @RequestParam("lon") Double longitude,
            @RequestParam(value = "days", defaultValue = "3") Integer days);
    
    @GetMapping("/api/weather/irrigation-advice")
    Map<String, Object> getIrrigationAdvice(
            @RequestParam("lat") Double latitude,
            @RequestParam("lon") Double longitude);
}
