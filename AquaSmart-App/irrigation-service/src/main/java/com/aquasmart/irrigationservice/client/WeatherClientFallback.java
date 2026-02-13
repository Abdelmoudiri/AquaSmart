package com.aquasmart.irrigationservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Fallback for Weather Client when service is unavailable
 */
@Component
@Slf4j
public class WeatherClientFallback implements WeatherClient {
    
    @Override
    public Map<String, Object> getCurrentWeather(Double latitude, Double longitude) {
        log.warn("Weather service unavailable, returning fallback for getCurrentWeather");
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("error", true);
        fallback.put("message", "Weather service temporarily unavailable");
        fallback.put("temperature", null);
        fallback.put("humidity", null);
        return fallback;
    }
    
    @Override
    public Map<String, Object> getForecast(Double latitude, Double longitude, Integer days) {
        log.warn("Weather service unavailable, returning fallback for getForecast");
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("error", true);
        fallback.put("message", "Weather service temporarily unavailable");
        fallback.put("forecast", null);
        return fallback;
    }
    
    @Override
    public Map<String, Object> getIrrigationAdvice(Double latitude, Double longitude) {
        log.warn("Weather service unavailable, returning fallback for getIrrigationAdvice");
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("error", true);
        fallback.put("message", "Weather service temporarily unavailable");
        fallback.put("shouldIrrigate", null);
        return fallback;
    }
}
