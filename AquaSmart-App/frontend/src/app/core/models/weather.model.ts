// ========== WEATHER MODELS ==========
// Modèles simples pour la météo

export interface CurrentWeather {
  temperature: number;
  feelsLike: number;
  humidity: number;
  pressure: number;
  windSpeed: number;
  windDirection: number;
  description: string;
  icon: string;
  cloudiness: number;
  clouds?: number; // Alias for cloudiness
  visibility: number;
  sunrise: string;
  sunset: string;
  timestamp: string;
  cityName?: string; // City name for display
}

export interface DailyForecast {
  date: string;
  tempMin: number;
  tempMax: number;
  minTemp?: number; // Alias for tempMin
  maxTemp?: number; // Alias for tempMax
  humidity: number;
  windSpeed: number;
  description: string;
  icon: string;
  rainProbability: number;
  precipitationProbability?: number; // Alias for rainProbability
  expectedRainfall: number;
}

export interface WeatherForecast {
  latitude: number;
  longitude: number;
  timezone: string;
  dailyForecasts: DailyForecast[];
}

export interface IrrigationAdvice {
  shouldIrrigate: boolean;
  confidence: number;
  currentConditions: {
    temperature: number;
    humidity: number;
    windSpeed: number;
    description: string;
  };
  rainForecast: {
    expectedInNext24h: boolean;
    expectedRainfall: number;
    probability: number;
  };
  recommendation: string;
  reasons: string[];
  reason?: string; // Alias for single reason display
  bestTimeToIrrigate?: string; // Best time suggestion
}
