package com.aquasmart.weatherservice.client;

import com.aquasmart.weatherservice.client.OpenWeatherMapModels.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class OpenWeatherMapClient {

    private final WebClient webClient;
    private final String apiKey;
    private final String units;
    private final String lang;

    public OpenWeatherMapClient(
            WebClient.Builder webClientBuilder,
            @Value("${openweathermap.api.base-url}") String baseUrl,
            @Value("${openweathermap.api.key}") String apiKey,
            @Value("${openweathermap.api.units}") String units,
            @Value("${openweathermap.api.lang}") String lang) {
        
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.apiKey = apiKey;
        this.units = units;
        this.lang = lang;
    }

    /**
     * Récupère la météo actuelle par coordonnées GPS
     */
    public Mono<CurrentWeatherApiResponse> getCurrentWeatherByCoords(Double lat, Double lon) {
        log.debug("Fetching current weather for coords: {}, {}", lat, lon);
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/weather")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("appid", apiKey)
                        .queryParam("units", units)
                        .queryParam("lang", lang)
                        .build())
                .retrieve()
                .bodyToMono(CurrentWeatherApiResponse.class)
                .doOnSuccess(response -> log.debug("Successfully fetched weather for: {}", response.getName()))
                .doOnError(error -> log.error("Error fetching weather: {}", error.getMessage()));
    }

    /**
     * Récupère la météo actuelle par nom de ville
     */
    public Mono<CurrentWeatherApiResponse> getCurrentWeatherByCity(String cityName) {
        log.debug("Fetching current weather for city: {}", cityName);
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/weather")
                        .queryParam("q", cityName)
                        .queryParam("appid", apiKey)
                        .queryParam("units", units)
                        .queryParam("lang", lang)
                        .build())
                .retrieve()
                .bodyToMono(CurrentWeatherApiResponse.class)
                .doOnSuccess(response -> log.debug("Successfully fetched weather for: {}", response.getName()))
                .doOnError(WebClientResponseException.class, 
                        error -> log.error("API Error {}: {}", error.getStatusCode(), error.getMessage()));
    }

    /**
     * Récupère les prévisions 5 jours / 3 heures par coordonnées
     */
    public Mono<ForecastApiResponse> getForecastByCoords(Double lat, Double lon) {
        log.debug("Fetching forecast for coords: {}, {}", lat, lon);
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/forecast")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("appid", apiKey)
                        .queryParam("units", units)
                        .queryParam("lang", lang)
                        .build())
                .retrieve()
                .bodyToMono(ForecastApiResponse.class)
                .doOnSuccess(response -> log.debug("Successfully fetched forecast for: {}", response.getCity().getName()))
                .doOnError(error -> log.error("Error fetching forecast: {}", error.getMessage()));
    }

    /**
     * Récupère les prévisions par nom de ville
     */
    public Mono<ForecastApiResponse> getForecastByCity(String cityName) {
        log.debug("Fetching forecast for city: {}", cityName);
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/forecast")
                        .queryParam("q", cityName)
                        .queryParam("appid", apiKey)
                        .queryParam("units", units)
                        .queryParam("lang", lang)
                        .build())
                .retrieve()
                .bodyToMono(ForecastApiResponse.class)
                .doOnSuccess(response -> log.debug("Successfully fetched forecast for: {}", response.getCity().getName()))
                .doOnError(error -> log.error("Error fetching forecast: {}", error.getMessage()));
    }
}
