package com.aquasmart.weatherservice.service.impl;

import com.aquasmart.weatherservice.client.OpenWeatherMapClient;
import com.aquasmart.weatherservice.client.OpenWeatherMapModels.*;
import com.aquasmart.weatherservice.dto.*;
import com.aquasmart.weatherservice.dto.IrrigationAdviceResponse.IrrigationUrgency;
import com.aquasmart.weatherservice.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherServiceImpl implements WeatherService {

    private final OpenWeatherMapClient weatherClient;

    @Override
    @Cacheable(value = "currentWeather", key = "#lat + '_' + #lon")
    public CurrentWeatherResponse getCurrentWeatherByCoords(Double lat, Double lon) {
        log.info("Fetching current weather for coordinates: {}, {}", lat, lon);
        
        CurrentWeatherApiResponse apiResponse = weatherClient.getCurrentWeatherByCoords(lat, lon).block();
        return mapToCurrentWeatherResponse(apiResponse);
    }

    @Override
    @Cacheable(value = "currentWeather", key = "#cityName")
    public CurrentWeatherResponse getCurrentWeatherByCity(String cityName) {
        log.info("Fetching current weather for city: {}", cityName);
        
        CurrentWeatherApiResponse apiResponse = weatherClient.getCurrentWeatherByCity(cityName).block();
        return mapToCurrentWeatherResponse(apiResponse);
    }

    @Override
    @Cacheable(value = "forecast", key = "#lat + '_' + #lon")
    public ForecastResponse getForecastByCoords(Double lat, Double lon) {
        log.info("Fetching forecast for coordinates: {}, {}", lat, lon);
        
        ForecastApiResponse apiResponse = weatherClient.getForecastByCoords(lat, lon).block();
        return mapToForecastResponse(apiResponse);
    }

    @Override
    @Cacheable(value = "forecast", key = "#cityName")
    public ForecastResponse getForecastByCity(String cityName) {
        log.info("Fetching forecast for city: {}", cityName);
        
        ForecastApiResponse apiResponse = weatherClient.getForecastByCity(cityName).block();
        return mapToForecastResponse(apiResponse);
    }

    @Override
    public DailyForecastResponse getDailyForecastByCoords(Double lat, Double lon, Integer days) {
        log.info("Fetching daily forecast for {} days at coordinates: {}, {}", days, lat, lon);
        
        ForecastResponse hourlyForecast = getForecastByCoords(lat, lon);
        return aggregateToDailyForecast(hourlyForecast, days);
    }

    @Override
    public IrrigationAdviceResponse getIrrigationAdvice(Double lat, Double lon) {
        log.info("Generating irrigation advice for coordinates: {}, {}", lat, lon);
        
        CurrentWeatherResponse current = getCurrentWeatherByCoords(lat, lon);
        ForecastResponse forecast = getForecastByCoords(lat, lon);
        
        return generateIrrigationAdvice(current, forecast);
    }

    @Override
    public IrrigationAdviceResponse getIrrigationAdviceByCity(String cityName) {
        log.info("Generating irrigation advice for city: {}", cityName);
        
        CurrentWeatherResponse current = getCurrentWeatherByCity(cityName);
        ForecastResponse forecast = getForecastByCity(cityName);
        
        return generateIrrigationAdvice(current, forecast);
    }

    // ========== MAPPING METHODS ==========

    private CurrentWeatherResponse mapToCurrentWeatherResponse(CurrentWeatherApiResponse api) {
        if (api == null) return null;
        
        Weather weather = api.getWeather() != null && !api.getWeather().isEmpty() 
                ? api.getWeather().get(0) : null;
        
        return CurrentWeatherResponse.builder()
                .cityName(api.getName())
                .country(api.getSys() != null ? api.getSys().getCountry() : null)
                .latitude(api.getCoord() != null ? api.getCoord().getLat() : null)
                .longitude(api.getCoord() != null ? api.getCoord().getLon() : null)
                .temperature(api.getMain() != null ? api.getMain().getTemp() : null)
                .feelsLike(api.getMain() != null ? api.getMain().getFeelsLike() : null)
                .tempMin(api.getMain() != null ? api.getMain().getTempMin() : null)
                .tempMax(api.getMain() != null ? api.getMain().getTempMax() : null)
                .humidity(api.getMain() != null ? api.getMain().getHumidity() : null)
                .pressure(api.getMain() != null ? api.getMain().getPressure() : null)
                .windSpeed(api.getWind() != null ? api.getWind().getSpeed() : null)
                .windDirection(api.getWind() != null ? api.getWind().getDeg() : null)
                .weatherMain(weather != null ? weather.getMain() : null)
                .weatherDescription(weather != null ? weather.getDescription() : null)
                .weatherIcon(weather != null ? weather.getIcon() : null)
                .clouds(api.getClouds() != null ? api.getClouds().getAll() : null)
                .visibility(api.getVisibility())
                .rain1h(api.getRain() != null ? api.getRain().getOneHour() : null)
                .rain3h(api.getRain() != null ? api.getRain().getThreeHours() : null)
                .sunrise(api.getSys() != null && api.getSys().getSunrise() != null 
                        ? LocalDateTime.ofInstant(Instant.ofEpochSecond(api.getSys().getSunrise()), ZoneId.systemDefault()) : null)
                .sunset(api.getSys() != null && api.getSys().getSunset() != null 
                        ? LocalDateTime.ofInstant(Instant.ofEpochSecond(api.getSys().getSunset()), ZoneId.systemDefault()) : null)
                .timestamp(api.getDt() != null 
                        ? LocalDateTime.ofInstant(Instant.ofEpochSecond(api.getDt()), ZoneId.systemDefault()) : null)
                .fetchedAt(LocalDateTime.now())
                .build();
    }

    private ForecastResponse mapToForecastResponse(ForecastApiResponse api) {
        if (api == null) return null;
        
        List<ForecastResponse.ForecastItem> items = api.getList().stream()
                .map(this::mapToForecastItem)
                .collect(Collectors.toList());
        
        return ForecastResponse.builder()
                .cityName(api.getCity() != null ? api.getCity().getName() : null)
                .country(api.getCity() != null ? api.getCity().getCountry() : null)
                .latitude(api.getCity() != null && api.getCity().getCoord() != null 
                        ? api.getCity().getCoord().getLat() : null)
                .longitude(api.getCity() != null && api.getCity().getCoord() != null 
                        ? api.getCity().getCoord().getLon() : null)
                .forecasts(items)
                .fetchedAt(LocalDateTime.now())
                .build();
    }

    private ForecastResponse.ForecastItem mapToForecastItem(ForecastListItem api) {
        Weather weather = api.getWeather() != null && !api.getWeather().isEmpty() 
                ? api.getWeather().get(0) : null;
        
        return ForecastResponse.ForecastItem.builder()
                .dateTime(api.getDt() != null 
                        ? LocalDateTime.ofInstant(Instant.ofEpochSecond(api.getDt()), ZoneId.systemDefault()) : null)
                .temperature(api.getMain() != null ? api.getMain().getTemp() : null)
                .feelsLike(api.getMain() != null ? api.getMain().getFeelsLike() : null)
                .tempMin(api.getMain() != null ? api.getMain().getTempMin() : null)
                .tempMax(api.getMain() != null ? api.getMain().getTempMax() : null)
                .humidity(api.getMain() != null ? api.getMain().getHumidity() : null)
                .pressure(api.getMain() != null ? api.getMain().getPressure() : null)
                .windSpeed(api.getWind() != null ? api.getWind().getSpeed() : null)
                .windDirection(api.getWind() != null ? api.getWind().getDeg() : null)
                .weatherMain(weather != null ? weather.getMain() : null)
                .weatherDescription(weather != null ? weather.getDescription() : null)
                .weatherIcon(weather != null ? weather.getIcon() : null)
                .clouds(api.getClouds() != null ? api.getClouds().getAll() : null)
                .pop(api.getPop())
                .rain3h(api.getRain() != null ? api.getRain().getThreeHours() : null)
                .snow3h(api.getSnow() != null ? api.getSnow().getThreeHours() : null)
                .build();
    }

    private DailyForecastResponse aggregateToDailyForecast(ForecastResponse hourly, Integer days) {
        if (hourly == null || hourly.getForecasts() == null) return null;
        
        // Grouper par date
        Map<LocalDate, List<ForecastResponse.ForecastItem>> byDate = hourly.getForecasts().stream()
                .filter(f -> f.getDateTime() != null)
                .collect(Collectors.groupingBy(f -> f.getDateTime().toLocalDate()));
        
        List<DailyForecastResponse.DailyItem> dailyItems = byDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .limit(days != null ? days : 5)
                .map(entry -> aggregateDay(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        
        return DailyForecastResponse.builder()
                .cityName(hourly.getCityName())
                .country(hourly.getCountry())
                .latitude(hourly.getLatitude())
                .longitude(hourly.getLongitude())
                .dailyForecasts(dailyItems)
                .fetchedAt(LocalDateTime.now())
                .build();
    }

    private DailyForecastResponse.DailyItem aggregateDay(LocalDate date, List<ForecastResponse.ForecastItem> items) {
        // Calculer les moyennes et min/max
        double avgTemp = items.stream().mapToDouble(ForecastResponse.ForecastItem::getTemperature).average().orElse(0);
        double minTemp = items.stream().mapToDouble(ForecastResponse.ForecastItem::getTempMin).min().orElse(0);
        double maxTemp = items.stream().mapToDouble(ForecastResponse.ForecastItem::getTempMax).max().orElse(0);
        int avgHumidity = (int) items.stream().mapToInt(ForecastResponse.ForecastItem::getHumidity).average().orElse(0);
        double avgWind = items.stream().mapToDouble(ForecastResponse.ForecastItem::getWindSpeed).average().orElse(0);
        double maxPop = items.stream().mapToDouble(f -> f.getPop() != null ? f.getPop() : 0).max().orElse(0);
        double totalRain = items.stream().mapToDouble(f -> f.getRain3h() != null ? f.getRain3h() : 0).sum();
        
        // Condition météo la plus fréquente
        String mainWeather = items.stream()
                .collect(Collectors.groupingBy(ForecastResponse.ForecastItem::getWeatherMain, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Clear");
        
        // Recommandation irrigation
        boolean shouldIrrigate = totalRain < 5 && avgHumidity < 60 && avgTemp > 20;
        String irrigationNote = generateDailyIrrigationNote(totalRain, avgHumidity, avgTemp, maxPop);
        
        return DailyForecastResponse.DailyItem.builder()
                .date(date)
                .tempDay(avgTemp)
                .tempMin(minTemp)
                .tempMax(maxTemp)
                .humidity(avgHumidity)
                .windSpeed(avgWind)
                .weatherMain(mainWeather)
                .weatherDescription(items.get(0).getWeatherDescription())
                .weatherIcon(items.get(0).getWeatherIcon())
                .pop(maxPop)
                .rainVolume(totalRain)
                .irrigationRecommended(shouldIrrigate)
                .irrigationNote(irrigationNote)
                .build();
    }

    private String generateDailyIrrigationNote(double rain, int humidity, double temp, double pop) {
        if (rain > 10) return "Pluie abondante prévue - pas d'irrigation nécessaire";
        if (rain > 5) return "Pluie modérée prévue - irrigation réduite";
        if (pop > 0.7) return "Forte probabilité de pluie - attendre";
        if (humidity > 70) return "Humidité élevée - irrigation légère";
        if (temp > 35) return "Températures élevées - irrigation recommandée tôt le matin";
        if (temp > 25) return "Irrigation normale recommandée";
        return "Conditions favorables - irrigation selon besoin";
    }

    private IrrigationAdviceResponse generateIrrigationAdvice(CurrentWeatherResponse current, ForecastResponse forecast) {
        // Calculer la pluie prévue dans les 24h
        double rain24h = 0;
        double maxPop = 0;
        double sumTemp = 0;
        int count = 0;
        
        if (forecast != null && forecast.getForecasts() != null) {
            LocalDateTime limit = LocalDateTime.now().plusHours(24);
            for (ForecastResponse.ForecastItem item : forecast.getForecasts()) {
                if (item.getDateTime() != null && item.getDateTime().isBefore(limit)) {
                    rain24h += item.getRain3h() != null ? item.getRain3h() : 0;
                    maxPop = Math.max(maxPop, item.getPop() != null ? item.getPop() : 0);
                    sumTemp += item.getTemperature() != null ? item.getTemperature() : 0;
                    count++;
                }
            }
        }
        
        double avgTemp24h = count > 0 ? sumTemp / count : (current != null ? current.getTemperature() : 20);
        
        // Déterminer la recommandation
        boolean shouldIrrigate;
        IrrigationUrgency urgency;
        String recommendation;
        String bestTime;
        double waterAmount;
        String reasoning;
        
        if (rain24h > 15) {
            shouldIrrigate = false;
            urgency = IrrigationUrgency.NONE;
            recommendation = "Pas d'irrigation nécessaire";
            bestTime = "N/A";
            waterAmount = 0;
            reasoning = String.format("Pluie abondante prévue (%.1f mm). L'irrigation naturelle sera suffisante.", rain24h);
        } else if (rain24h > 5) {
            shouldIrrigate = false;
            urgency = IrrigationUrgency.LOW;
            recommendation = "Irrigation non recommandée";
            bestTime = "Attendre après la pluie";
            waterAmount = 0;
            reasoning = String.format("Pluie modérée prévue (%.1f mm). Attendre et réévaluer après.", rain24h);
        } else if (maxPop > 0.7) {
            shouldIrrigate = false;
            urgency = IrrigationUrgency.LOW;
            recommendation = "Attendre - pluie probable";
            bestTime = "À réévaluer demain";
            waterAmount = 0;
            reasoning = String.format("Probabilité de pluie élevée (%.0f%%). Préférable d'attendre.", maxPop * 100);
        } else if (current != null && current.getHumidity() != null && current.getHumidity() < 40) {
            shouldIrrigate = true;
            urgency = avgTemp24h > 30 ? IrrigationUrgency.HIGH : IrrigationUrgency.MEDIUM;
            recommendation = "Irrigation recommandée";
            bestTime = "Tôt le matin (6h-8h) ou soir (18h-20h)";
            waterAmount = avgTemp24h > 30 ? 120 : 100;
            reasoning = String.format("Humidité basse (%d%%) et peu de pluie prévue. Irrigation nécessaire.", current.getHumidity());
        } else if (avgTemp24h > 30) {
            shouldIrrigate = true;
            urgency = IrrigationUrgency.MEDIUM;
            recommendation = "Irrigation conseillée";
            bestTime = "Tôt le matin (6h-8h)";
            waterAmount = 110;
            reasoning = String.format("Températures élevées prévues (%.1f°C en moyenne). Irrigation préventive recommandée.", avgTemp24h);
        } else {
            shouldIrrigate = true;
            urgency = IrrigationUrgency.LOW;
            recommendation = "Irrigation normale";
            bestTime = "Matin (6h-9h) ou soir (17h-20h)";
            waterAmount = 100;
            reasoning = "Conditions normales. Suivre le programme d'irrigation habituel.";
        }
        
        return IrrigationAdviceResponse.builder()
                .cityName(current != null ? current.getCityName() : null)
                .latitude(current != null ? current.getLatitude() : null)
                .longitude(current != null ? current.getLongitude() : null)
                .shouldIrrigate(shouldIrrigate)
                .recommendation(recommendation)
                .urgency(urgency)
                .currentTemperature(current != null ? current.getTemperature() : null)
                .currentHumidity(current != null ? current.getHumidity() : null)
                .currentWeather(current != null ? current.getWeatherDescription() : null)
                .rainExpected24h(rain24h)
                .rainProbability(maxPop)
                .avgTemperature24h(avgTemp24h)
                .bestTimeToIrrigate(bestTime)
                .suggestedWaterAmount(waterAmount)
                .reasoning(reasoning)
                .generatedAt(LocalDateTime.now())
                .build();
    }
}
