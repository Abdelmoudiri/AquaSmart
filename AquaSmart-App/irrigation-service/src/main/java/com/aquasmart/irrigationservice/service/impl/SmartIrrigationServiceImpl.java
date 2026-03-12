package com.aquasmart.irrigationservice.service.impl;

import com.aquasmart.irrigationservice.client.WeatherClient;
import com.aquasmart.irrigationservice.dto.IrrigationRecommendation;
import com.aquasmart.irrigationservice.service.ai.GeminiRecommendationClient;
import com.aquasmart.irrigationservice.service.SmartIrrigationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmartIrrigationServiceImpl implements SmartIrrigationService {
    
    private final WeatherClient weatherClient;
    private final GeminiRecommendationClient geminiRecommendationClient;
    
    @Value("${irrigation.defaults.min-soil-moisture:30.0}")
    private Double minSoilMoisture;
    
    @Value("${irrigation.defaults.max-soil-moisture:70.0}")
    private Double maxSoilMoisture;
    
    @Value("${irrigation.defaults.rain-threshold-mm:5.0}")
    private Double rainThreshold;
    
    @Value("${irrigation.defaults.wind-threshold-kmh:25.0}")
    private Double windThreshold;
    
    @Value("${irrigation.defaults.temp-threshold-max:35.0}")
    private Double tempThresholdMax;
    
    @Override
    public IrrigationRecommendation getRecommendation(Long parcelId, Long farmId,
                                                       Double latitude, Double longitude,
                                                       Double currentSoilMoisture) {
        log.info("Getting irrigation recommendation for parcel: {}", parcelId);
        
        List<String> reasons = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        boolean shouldIrrigate = false;
        int confidenceScore = 80;
        
        // Get current weather
        Map<String, Object> weatherData = null;
        Double temperature = null;
        Double humidity = null;
        Double windSpeed = null;
        Double rainProbability = null;
        Double expectedRainfall = null;
        String weatherDescription = "Unknown";
        
        try {
            weatherData = weatherClient.getCurrentWeather(latitude, longitude);
            if (weatherData != null && !weatherData.containsKey("error")) {
                temperature = getDouble(weatherData, "temperature");
                humidity = getDouble(weatherData, "humidity");
                windSpeed = getDouble(weatherData, "windSpeed");
                weatherDescription = getString(weatherData, "description", "Unknown");
            }
            
            // Get forecast for rain prediction
            Map<String, Object> forecastData = weatherClient.getForecast(latitude, longitude, 1);
            if (forecastData != null && !forecastData.containsKey("error")) {
                rainProbability = getDouble(forecastData, "rainProbability");
                expectedRainfall = getDouble(forecastData, "expectedRainfall");
            }
        } catch (Exception e) {
            log.warn("Could not fetch weather data: {}", e.getMessage());
            warnings.add("Weather data unavailable - using default recommendations");
            confidenceScore -= 30;
        }
        
        // Analyze soil moisture
        if (currentSoilMoisture != null) {
            if (currentSoilMoisture < minSoilMoisture) {
                shouldIrrigate = true;
                reasons.add(String.format("Soil moisture (%.1f%%) is below minimum threshold (%.1f%%)", 
                        currentSoilMoisture, minSoilMoisture));
            } else if (currentSoilMoisture >= maxSoilMoisture) {
                shouldIrrigate = false;
                reasons.add(String.format("Soil moisture (%.1f%%) is at or above maximum threshold (%.1f%%)", 
                        currentSoilMoisture, maxSoilMoisture));
            } else {
                // In between - consider other factors
                if (currentSoilMoisture < (minSoilMoisture + maxSoilMoisture) / 2) {
                    shouldIrrigate = true;
                    reasons.add(String.format("Soil moisture (%.1f%%) is below optimal level", currentSoilMoisture));
                }
            }
        } else {
            warnings.add("No soil moisture data available - recommendation based on time and weather only");
            confidenceScore -= 20;
        }
        
        // Check rain forecast
        if (expectedRainfall != null && expectedRainfall >= rainThreshold) {
            shouldIrrigate = false;
            reasons.add(String.format("Rain expected (%.1fmm) - skipping irrigation", expectedRainfall));
        } else if (rainProbability != null && rainProbability > 70) {
            warnings.add(String.format("High rain probability (%.0f%%) - consider delaying irrigation", rainProbability));
        }
        
        // Check wind conditions
        if (windSpeed != null && windSpeed > windThreshold) {
            if (shouldIrrigate) {
                warnings.add(String.format("High wind speed (%.1f km/h) - irrigation efficiency may be reduced", windSpeed));
            }
        }
        
        // Check temperature
        if (temperature != null && temperature > tempThresholdMax) {
            if (shouldIrrigate) {
                warnings.add(String.format("High temperature (%.1f°C) - consider irrigating during cooler hours", temperature));
            }
        }
        
        // Calculate optimal start time
        LocalDateTime optimalStartTime = calculateOptimalStartTime(temperature);
        
        // Calculate recommended water amount
        Double recommendedWaterAmount = calculateOptimalWaterAmount(parcelId, currentSoilMoisture, 
                temperature, humidity);
        
        // Calculate recommended duration (assuming ~10L/min for drip irrigation)
        int recommendedDuration = recommendedWaterAmount != null ? 
                (int) Math.ceil(recommendedWaterAmount / 10) : 30;
        
        IrrigationRecommendation baseRecommendation = IrrigationRecommendation.builder()
                .parcelId(parcelId)
                .farmId(farmId)
                .shouldIrrigate(shouldIrrigate)
                .recommendedDurationMinutes(recommendedDuration)
                .recommendedWaterAmount(recommendedWaterAmount)
                .optimalStartTime(optimalStartTime)
                .confidenceScore(confidenceScore)
                .conditions(IrrigationRecommendation.CurrentConditions.builder()
                        .soilMoisture(currentSoilMoisture)
                        .temperature(temperature)
                        .humidity(humidity)
                        .windSpeed(windSpeed)
                        .rainProbability(rainProbability)
                        .expectedRainfall(expectedRainfall)
                        .weatherDescription(weatherDescription)
                        .build())
                .reasons(reasons)
                .warnings(warnings)
                .build();

            return geminiRecommendationClient.applyOn(baseRecommendation);
    }
    
    @Override
    public boolean shouldSkipIrrigation(Double latitude, Double longitude) {
        try {
            Map<String, Object> forecastData = weatherClient.getForecast(latitude, longitude, 1);
            if (forecastData != null && !forecastData.containsKey("error")) {
                Double expectedRainfall = getDouble(forecastData, "expectedRainfall");
                if (expectedRainfall != null && expectedRainfall >= rainThreshold) {
                    log.info("Skipping irrigation - rain expected: {}mm", expectedRainfall);
                    return true;
                }
            }
        } catch (Exception e) {
            log.warn("Could not check weather for skip decision: {}", e.getMessage());
        }
        return false;
    }
    
    @Override
    public Double calculateOptimalWaterAmount(Long parcelId, Double soilMoisture,
                                               Double temperature, Double humidity) {
        // Base amount in liters per square meter
        double baseAmount = 5.0;
        
        // Adjust based on soil moisture
        if (soilMoisture != null) {
            double moistureDeficit = maxSoilMoisture - soilMoisture;
            baseAmount = Math.max(1.0, moistureDeficit * 0.15);
        }
        
        // Adjust based on temperature
        if (temperature != null) {
            if (temperature > 30) {
                baseAmount *= 1.2;
            } else if (temperature > 25) {
                baseAmount *= 1.1;
            } else if (temperature < 15) {
                baseAmount *= 0.8;
            }
        }
        
        // Adjust based on humidity
        if (humidity != null) {
            if (humidity < 40) {
                baseAmount *= 1.15;
            } else if (humidity > 70) {
                baseAmount *= 0.9;
            }
        }
        
        return Math.round(baseAmount * 100.0) / 100.0;
    }
    
    @Override
    public Integer evaluateEfficiency(Double plannedWater, Double actualWater,
                                       Double soilMoistureBefore, Double soilMoistureAfter,
                                       Double temperature) {
        if (plannedWater == null || actualWater == null) {
            return 50; // Default score if data missing
        }
        
        int score = 70; // Base score
        
        // Water usage efficiency (using less than planned is good)
        double waterRatio = actualWater / plannedWater;
        if (waterRatio <= 0.8) {
            score += 15;
        } else if (waterRatio <= 1.0) {
            score += 10;
        } else if (waterRatio <= 1.2) {
            score -= 5;
        } else {
            score -= 15;
        }
        
        // Soil moisture improvement
        if (soilMoistureBefore != null && soilMoistureAfter != null) {
            double improvement = soilMoistureAfter - soilMoistureBefore;
            if (improvement >= 20 && soilMoistureAfter <= maxSoilMoisture) {
                score += 15;
            } else if (improvement >= 10) {
                score += 10;
            } else if (improvement < 5) {
                score -= 10;
            }
            
            // Penalty for over-watering
            if (soilMoistureAfter > maxSoilMoisture) {
                score -= 10;
            }
        }
        
        // Bonus for irrigating during optimal hours
        LocalTime now = LocalTime.now();
        if ((now.getHour() >= 6 && now.getHour() <= 8) || 
            (now.getHour() >= 18 && now.getHour() <= 20)) {
            score += 5;
        } else if (now.getHour() >= 11 && now.getHour() <= 15) {
            score -= 5; // Penalty for midday irrigation
        }
        
        return Math.max(0, Math.min(100, score));
    }
    
    private LocalDateTime calculateOptimalStartTime(Double temperature) {
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        
        // Prefer early morning (6-8 AM) or evening (6-8 PM)
        if (currentTime.isBefore(LocalTime.of(6, 0))) {
            return now.withHour(6).withMinute(0).withSecond(0);
        } else if (currentTime.isBefore(LocalTime.of(8, 0))) {
            return now; // Good time, start now
        } else if (currentTime.isBefore(LocalTime.of(18, 0))) {
            return now.withHour(18).withMinute(0).withSecond(0);
        } else if (currentTime.isBefore(LocalTime.of(20, 0))) {
            return now; // Good time, start now
        } else {
            // After 8 PM, schedule for next morning
            return now.plusDays(1).withHour(6).withMinute(0).withSecond(0);
        }
    }
    
    private Double getDouble(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return null;
    }
    
    private String getString(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }
}
