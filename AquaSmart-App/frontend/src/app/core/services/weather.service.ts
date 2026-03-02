import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, of, catchError } from 'rxjs';
import { CurrentWeather, WeatherForecast, IrrigationAdvice, DailyForecast } from '../models/weather.model';

const API_URL = 'http://localhost:8080/api/weather';

// Mode développement - utiliser les données mock si l'API n'est pas disponible
const USE_MOCK_DATA = true;

@Injectable({
  providedIn: 'root'
})
export class WeatherService {

  constructor(private http: HttpClient) { }

  // Générer des données météo simulées pour le développement
  private getMockCurrentWeather(cityName?: string): CurrentWeather {
    const conditions = [
      { desc: 'Ensoleillé', icon: '01d' },
      { desc: 'Partiellement nuageux', icon: '02d' },
      { desc: 'Nuageux', icon: '03d' },
      { desc: 'Ciel dégagé', icon: '01d' }
    ];
    const condition = conditions[Math.floor(Math.random() * conditions.length)];
    
    return {
      temperature: Math.round(20 + Math.random() * 15),
      feelsLike: Math.round(22 + Math.random() * 12),
      humidity: Math.round(40 + Math.random() * 40),
      pressure: Math.round(1010 + Math.random() * 20),
      windSpeed: Math.round(5 + Math.random() * 20),
      windDirection: Math.round(Math.random() * 360),
      description: condition.desc,
      icon: condition.icon,
      cloudiness: Math.round(Math.random() * 100),
      clouds: Math.round(Math.random() * 100),
      visibility: 10000,
      sunrise: '06:30',
      sunset: '19:45',
      timestamp: new Date().toISOString(),
      cityName: cityName || 'Marrakech'
    };
  }

  private getMockForecast(): WeatherForecast {
    const forecasts: DailyForecast[] = [];
    const icons = ['01d', '02d', '03d', '04d', '10d'];
    const descriptions = ['Ensoleillé', 'Nuages épars', 'Nuageux', 'Couvert', 'Pluie légère'];
    
    for (let i = 0; i < 5; i++) {
      const date = new Date();
      date.setDate(date.getDate() + i);
      const idx = Math.floor(Math.random() * icons.length);
      
      forecasts.push({
        date: date.toISOString().split('T')[0],
        tempMin: Math.round(15 + Math.random() * 8),
        tempMax: Math.round(25 + Math.random() * 10),
        minTemp: Math.round(15 + Math.random() * 8),
        maxTemp: Math.round(25 + Math.random() * 10),
        humidity: Math.round(40 + Math.random() * 40),
        windSpeed: Math.round(5 + Math.random() * 15),
        description: descriptions[idx],
        icon: icons[idx],
        rainProbability: Math.round(Math.random() * 30),
        precipitationProbability: Math.round(Math.random() * 30),
        expectedRainfall: Math.round(Math.random() * 5)
      });
    }
    
    return {
      latitude: 31.6295,
      longitude: -7.9811,
      timezone: 'Africa/Casablanca',
      dailyForecasts: forecasts
    };
  }

  private getMockIrrigationAdvice(): IrrigationAdvice {
    const shouldIrrigate = Math.random() > 0.3;
    return {
      shouldIrrigate,
      confidence: Math.round(70 + Math.random() * 30),
      currentConditions: {
        temperature: Math.round(20 + Math.random() * 15),
        humidity: Math.round(40 + Math.random() * 40),
        windSpeed: Math.round(5 + Math.random() * 15),
        description: 'Ensoleillé'
      },
      rainForecast: {
        expectedInNext24h: Math.random() > 0.7,
        expectedRainfall: Math.round(Math.random() * 10),
        probability: Math.round(Math.random() * 40)
      },
      recommendation: shouldIrrigate 
        ? 'Irrigation recommandée ce matin entre 6h et 8h pour minimiser l\'évaporation.'
        : 'Pas d\'irrigation nécessaire - pluie prévue dans les prochaines 24h.',
      reasons: shouldIrrigate 
        ? ['Température élevée prévue', 'Faible humidité du sol', 'Pas de pluie attendue']
        : ['Pluie prévue', 'Humidité suffisante'],
      reason: shouldIrrigate ? 'Conditions sèches détectées' : 'Pluie imminente',
      bestTimeToIrrigate: shouldIrrigate ? '06:00 - 08:00' : undefined
    };
  }

  // Récupérer la météo actuelle par coordonnées
  getCurrentWeather(lat: number, lon: number): Observable<CurrentWeather> {
    if (USE_MOCK_DATA) {
      return of(this.getMockCurrentWeather());
    }
    return this.http.get<CurrentWeather>(`${API_URL}/current?lat=${lat}&lon=${lon}`).pipe(
      catchError(() => of(this.getMockCurrentWeather()))
    );
  }

  // Récupérer la météo actuelle par nom de ville
  getCurrentWeatherByCity(city: string): Observable<CurrentWeather> {
    if (USE_MOCK_DATA) {
      return of(this.getMockCurrentWeather(city));
    }
    return this.http.get<CurrentWeather>(`${API_URL}/current/city/${city}`).pipe(
      catchError(() => of(this.getMockCurrentWeather(city)))
    );
  }

  // Récupérer les prévisions sur 5 jours
  getForecast(lat: number, lon: number): Observable<WeatherForecast> {
    if (USE_MOCK_DATA) {
      return of(this.getMockForecast());
    }
    return this.http.get<WeatherForecast>(`${API_URL}/forecast?lat=${lat}&lon=${lon}`).pipe(
      catchError(() => of(this.getMockForecast()))
    );
  }

  // Récupérer les prévisions par nom de ville
  getForecastByCity(city: string): Observable<WeatherForecast> {
    if (USE_MOCK_DATA) {
      return of(this.getMockForecast());
    }
    return this.http.get<WeatherForecast>(`${API_URL}/forecast/city/${city}`).pipe(
      catchError(() => of(this.getMockForecast()))
    );
  }

  // Récupérer les conseils d'irrigation basés sur la météo
  getIrrigationAdvice(lat: number, lon: number): Observable<IrrigationAdvice> {
    if (USE_MOCK_DATA) {
      return of(this.getMockIrrigationAdvice());
    }
    return this.http.get<IrrigationAdvice>(`${API_URL}/irrigation-advice?lat=${lat}&lon=${lon}`).pipe(
      catchError(() => of(this.getMockIrrigationAdvice()))
    );
  }
}
