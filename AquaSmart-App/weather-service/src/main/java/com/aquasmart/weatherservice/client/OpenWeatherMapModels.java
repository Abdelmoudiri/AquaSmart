package com.aquasmart.weatherservice.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Modèles pour parser les réponses de l'API OpenWeatherMap
 */
public class OpenWeatherMapModels {

    @Data
    public static class CurrentWeatherApiResponse {
        private Coord coord;
        private List<Weather> weather;
        private String base;
        private Main main;
        private Integer visibility;
        private Wind wind;
        private Rain rain;
        private Clouds clouds;
        private Long dt;
        private Sys sys;
        private Integer timezone;
        private Long id;
        private String name;
        private Integer cod;
    }

    @Data
    public static class ForecastApiResponse {
        private String cod;
        private Integer message;
        private Integer cnt;
        private List<ForecastListItem> list;
        private City city;
    }

    @Data
    public static class Coord {
        private Double lon;
        private Double lat;
    }

    @Data
    public static class Weather {
        private Integer id;
        private String main;
        private String description;
        private String icon;
    }

    @Data
    public static class Main {
        private Double temp;
        @JsonProperty("feels_like")
        private Double feelsLike;
        @JsonProperty("temp_min")
        private Double tempMin;
        @JsonProperty("temp_max")
        private Double tempMax;
        private Integer pressure;
        private Integer humidity;
        @JsonProperty("sea_level")
        private Integer seaLevel;
        @JsonProperty("grnd_level")
        private Integer grndLevel;
    }

    @Data
    public static class Wind {
        private Double speed;
        private Integer deg;
        private Double gust;
    }

    @Data
    public static class Rain {
        @JsonProperty("1h")
        private Double oneHour;
        @JsonProperty("3h")
        private Double threeHours;
    }

    @Data
    public static class Snow {
        @JsonProperty("1h")
        private Double oneHour;
        @JsonProperty("3h")
        private Double threeHours;
    }

    @Data
    public static class Clouds {
        private Integer all;
    }

    @Data
    public static class Sys {
        private Integer type;
        private Integer id;
        private String country;
        private Long sunrise;
        private Long sunset;
    }

    @Data
    public static class ForecastListItem {
        private Long dt;
        private Main main;
        private List<Weather> weather;
        private Clouds clouds;
        private Wind wind;
        private Integer visibility;
        private Double pop;  // Probability of precipitation
        private Rain rain;
        private Snow snow;
        private Sys sys;
        @JsonProperty("dt_txt")
        private String dtTxt;
    }

    @Data
    public static class City {
        private Long id;
        private String name;
        private Coord coord;
        private String country;
        private Integer population;
        private Integer timezone;
        private Long sunrise;
        private Long sunset;
    }
}
