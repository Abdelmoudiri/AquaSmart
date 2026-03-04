import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { WeatherService } from '../../core/services/weather.service';
import { FarmService } from '../../core/services/farm.service';
import { AuthService } from '../../core/services/auth.service';
import { CurrentWeather, WeatherForecast, IrrigationAdvice } from '../../core/models/weather.model';
import { Farm } from '../../core/models/farm.model';
import { NavbarComponent } from '../../core/components/navbar/navbar.component';

@Component({
  selector: 'app-weather',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  template: `
    <app-navbar></app-navbar>
    <div class="p-6">
      <!-- Header -->
      <h1 class="text-2xl font-bold text-gray-800 mb-6">Météo</h1>

      <!-- Farm Selection -->
      <div class="bg-white rounded-xl shadow-sm p-6 mb-6">
        <div class="flex flex-wrap gap-4 items-end">
          <div class="flex-1 min-w-[200px]">
            <label class="block text-sm font-medium text-gray-700 mb-2">Sélectionner une ferme</label>
            <select [(ngModel)]="selectedFarmId" 
                    (change)="onFarmChange()"
                    class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary">
              <option [value]="null">-- Choisir une ferme --</option>
              <option *ngFor="let farm of farms" [value]="farm.id">{{ farm.name }}</option>
            </select>
          </div>
          <div class="text-center">
            <span class="text-gray-400">ou</span>
          </div>
          <div class="flex-1 min-w-[200px]">
            <label class="block text-sm font-medium text-gray-700 mb-2">Rechercher par ville</label>
            <div class="flex gap-2">
              <input type="text" 
                     [(ngModel)]="citySearch" 
                     placeholder="Ex: Marrakech"
                     class="flex-1 px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary">
              <button (click)="searchByCity()" 
                      class="bg-primary text-white px-4 py-2 rounded-lg hover:bg-primary-dark">
                Rechercher
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Loading -->
      <div *ngIf="loading" class="text-center py-8">
        <p class="text-gray-500">Chargement de la météo...</p>
      </div>

      <!-- Error -->
      <div *ngIf="error" class="bg-red-100 text-red-700 p-4 rounded-lg mb-4">
        {{ error }}
      </div>

      <!-- Weather Data -->
      <div *ngIf="currentWeather && !loading" class="space-y-6">
        
        <!-- Current Weather -->
        <div class="bg-gradient-to-r from-blue-500 to-blue-600 rounded-xl shadow-lg p-8 text-white">
          <div class="flex flex-wrap justify-between items-center">
            <div>
              <h2 class="text-3xl font-bold mb-2">{{ currentWeather.cityName }}</h2>
              <p class="text-blue-100 capitalize">{{ currentWeather.description }}</p>
            </div>
            <div class="text-center">
              <p class="text-6xl font-bold">{{ currentWeather.temperature | number:'1.0-0' }}°</p>
              <p class="text-blue-100">Ressenti {{ currentWeather.feelsLike | number:'1.0-0' }}°</p>
            </div>
          </div>
          
          <div class="grid grid-cols-2 md:grid-cols-4 gap-4 mt-8">
            <div class="text-center">
              <p class="text-blue-200 text-sm">Humidité</p>
              <p class="text-2xl font-semibold">{{ currentWeather.humidity }}%</p>
            </div>
            <div class="text-center">
              <p class="text-blue-200 text-sm">Vent</p>
              <p class="text-2xl font-semibold">{{ currentWeather.windSpeed }} km/h</p>
            </div>
            <div class="text-center">
              <p class="text-blue-200 text-sm">Pression</p>
              <p class="text-2xl font-semibold">{{ currentWeather.pressure }} hPa</p>
            </div>
            <div class="text-center">
              <p class="text-blue-200 text-sm">Nuages</p>
              <p class="text-2xl font-semibold">{{ currentWeather.clouds }}%</p>
            </div>
          </div>
        </div>

        <!-- Irrigation Advice -->
        <div *ngIf="irrigationAdvice" class="bg-white rounded-xl shadow-sm p-6">
          <h3 class="text-xl font-semibold text-gray-800 mb-4">Conseil d'irrigation</h3>
          <div class="flex items-start gap-4">
            <div class="text-4xl">
              {{ irrigationAdvice.shouldIrrigate ? '💧' : '☀️' }}
            </div>
            <div>
              <p class="font-medium text-lg"
                 [class.text-blue-600]="irrigationAdvice.shouldIrrigate"
                 [class.text-orange-600]="!irrigationAdvice.shouldIrrigate">
                {{ irrigationAdvice.shouldIrrigate ? 'Irrigation recommandée' : "Pas d'irrigation nécessaire" }}
              </p>
              <p class="text-gray-600 mt-1">{{ irrigationAdvice.reason }}</p>
              <div *ngIf="irrigationAdvice.shouldIrrigate && irrigationAdvice.bestTimeToIrrigate" 
                   class="mt-2 text-sm text-gray-500">
                <p>Meilleur moment: {{ irrigationAdvice.bestTimeToIrrigate }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Forecast -->
        <div *ngIf="forecast" class="bg-white rounded-xl shadow-sm p-6">
          <h3 class="text-xl font-semibold text-gray-800 mb-4">Prévisions sur 5 jours</h3>
          <div class="grid grid-cols-2 md:grid-cols-5 gap-4">
            <div *ngFor="let day of forecast.dailyForecasts" 
                 class="text-center p-4 bg-gray-50 rounded-lg">
              <p class="font-medium text-gray-800">{{ formatDay(day.date) }}</p>
              <p class="text-3xl my-2">{{ getWeatherEmoji(day.description) }}</p>
              <p class="text-sm text-gray-600 capitalize">{{ day.description }}</p>
              <div class="mt-2">
                <span class="text-red-500 font-semibold">{{ day.maxTemp | number:'1.0-0' }}°</span>
                <span class="text-gray-400 mx-1">/</span>
                <span class="text-blue-500">{{ day.minTemp | number:'1.0-0' }}°</span>
              </div>
              <p class="text-xs text-gray-400 mt-1">💧 {{ day.precipitationProbability }}%</p>
            </div>
          </div>
        </div>
      </div>

      <!-- No Data -->
      <div *ngIf="!currentWeather && !loading && !error" 
           class="text-center py-12 bg-white rounded-xl shadow-sm">
        <p class="text-gray-500">Sélectionnez une ferme ou recherchez une ville pour voir la météo</p>
      </div>
    </div>
  `
})
export class WeatherComponent implements OnInit {
  private weatherService = inject(WeatherService);
  private farmService = inject(FarmService);
  private authService = inject(AuthService);

  farms: Farm[] = [];
  selectedFarmId: number | null = null;
  citySearch = '';

  currentWeather: CurrentWeather | null = null;
  forecast: WeatherForecast | null = null;
  irrigationAdvice: IrrigationAdvice | null = null;

  loading = false;
  error = '';

  ngOnInit() {
    this.loadFarms();
  }

  loadFarms() {
    const user = this.authService.currentUser();
    if (!user) return;

    // Utiliser ownerId par défaut (1) pour le développement
    this.farmService.getFarmsByOwner(1).subscribe({
      next: (farms) => {
        this.farms = farms;
      }
    });
  }

  onFarmChange() {
    if (!this.selectedFarmId) return;

    const farm = this.farms.find(f => f.id === +this.selectedFarmId!);
    if (farm?.latitude && farm?.longitude) {
      this.loadWeatherByCoords(farm.latitude, farm.longitude);
    } else {
      this.error = 'Cette ferme n\'a pas de coordonnées GPS';
    }
  }

  searchByCity() {
    if (!this.citySearch.trim()) return;

    this.loading = true;
    this.error = '';
    this.selectedFarmId = null;

    this.weatherService.getCurrentWeatherByCity(this.citySearch).subscribe({
      next: (weather) => {
        this.currentWeather = weather;
        this.loadForecastByCity(this.citySearch);
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Ville non trouvée';
        this.loading = false;
        console.error(err);
      }
    });
  }

  loadWeatherByCoords(lat: number, lon: number) {
    this.loading = true;
    this.error = '';

    this.weatherService.getCurrentWeather(lat, lon).subscribe({
      next: (weather) => {
        this.currentWeather = weather;
        this.loadForecast(lat, lon);
        this.loadIrrigationAdvice(lat, lon);
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement de la météo';
        this.loading = false;
        console.error(err);
      }
    });
  }

  loadForecast(lat: number, lon: number) {
    this.weatherService.getForecast(lat, lon).subscribe({
      next: (forecast) => {
        this.forecast = forecast;
      }
    });
  }

  loadForecastByCity(city: string) {
    this.weatherService.getForecastByCity(city).subscribe({
      next: (forecast) => {
        this.forecast = forecast;
      }
    });
  }

  loadIrrigationAdvice(lat: number, lon: number) {
    this.weatherService.getIrrigationAdvice(lat, lon).subscribe({
      next: (advice) => {
        this.irrigationAdvice = advice;
      }
    });
  }

  formatDay(date: string): string {
    const d = new Date(date);
    return d.toLocaleDateString('fr-FR', { weekday: 'short', day: 'numeric' });
  }

  getWeatherEmoji(description: string): string {
    const desc = description.toLowerCase();
    if (desc.includes('soleil') || desc.includes('clear') || desc.includes('sunny')) return '☀️';
    if (desc.includes('nuage') || desc.includes('cloud')) return '☁️';
    if (desc.includes('pluie') || desc.includes('rain')) return '🌧️';
    if (desc.includes('orage') || desc.includes('thunder')) return '⛈️';
    if (desc.includes('neige') || desc.includes('snow')) return '❄️';
    if (desc.includes('brouillard') || desc.includes('fog') || desc.includes('mist')) return '🌫️';
    return '🌤️';
  }
}
