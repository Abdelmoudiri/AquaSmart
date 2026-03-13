import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FarmService } from '../../../core/services/farm.service';
import { WeatherService } from '../../../core/services/weather.service';
import { AlertService } from '../../../core/services/alert.service';
import { IrrigationService } from '../../../core/services/irrigation.service';
import { Farm, Parcel } from '../../../core/models/farm.model';
import { CurrentWeather } from '../../../core/models/weather.model';
import { AlertSummary } from '../../../core/models/alert.model';
import { IrrigationRecommendation } from '../../../core/models/irrigation.model';
import { NavbarComponent } from '../../../core/components/navbar/navbar.component';

@Component({
  selector: 'app-farm-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent],
  template: `
    <app-navbar></app-navbar>
    <div class="p-6">
      <!-- Loading -->
      <div *ngIf="loading" class="text-center py-8">
        <p class="text-gray-500">Chargement...</p>
      </div>

      <!-- Error -->
      <div *ngIf="error" class="bg-red-100 text-red-700 p-4 rounded-lg mb-4">
        {{ error }}
      </div>

      <div *ngIf="farm">
        <!-- Header -->
        <div class="flex justify-between items-start mb-6">
          <div>
            <a routerLink="/farms" class="text-primary hover:underline text-sm mb-2 inline-block">
              ← Retour aux fermes
            </a>
            <h1 class="text-3xl font-bold text-gray-800">{{ farm.name }}</h1>
            <p class="text-gray-500">{{ farm.location }}</p>
          </div>
          <div class="flex gap-2">
            <a [routerLink]="['/farms', farm.id, 'edit']" 
               class="bg-gray-100 text-gray-700 px-4 py-2 rounded-lg hover:bg-gray-200">
              Modifier
            </a>
            <button (click)="deleteFarm()" 
                    class="bg-red-100 text-red-600 px-4 py-2 rounded-lg hover:bg-red-200">
              Supprimer
            </button>
          </div>
        </div>

        <!-- Info Cards Row -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          
          <!-- Farm Info -->
          <div class="bg-white rounded-xl shadow-sm p-6">
            <h3 class="text-lg font-semibold text-gray-800 mb-4">Informations</h3>
            <div class="space-y-3 text-sm">
              <div class="flex justify-between">
                <span class="text-gray-500">Surface totale</span>
                <span class="font-medium">{{ farm.totalArea }} ha</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-500">Parcelles</span>
                <span class="font-medium">{{ farm.parcels?.length || 0 }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-gray-500">Statut</span>
                <span class="font-medium" [class.text-green-600]="farm.active" [class.text-gray-500]="!farm.active">
                  {{ farm.active ? 'Active' : 'Inactive' }}
                </span>
              </div>
            </div>
          </div>

          <!-- Weather -->
          <div class="bg-white rounded-xl shadow-sm p-6">
            <h3 class="text-lg font-semibold text-gray-800 mb-4">Météo</h3>
            <div *ngIf="weather" class="text-center">
              <p class="text-4xl font-bold text-primary-dark">{{ weather.temperature }}°C</p>
              <p class="text-gray-500 capitalize">{{ weather.description }}</p>
              <div class="mt-4 text-sm text-gray-600">
                <p>Humidité: {{ weather.humidity }}%</p>
                <p>Vent: {{ weather.windSpeed }} km/h</p>
              </div>
            </div>
            <div *ngIf="!weather && !weatherLoading" class="text-center text-gray-500">
              <p>Météo non disponible</p>
            </div>
            <div *ngIf="weatherLoading" class="text-center text-gray-400">
              Chargement...
            </div>
          </div>

          <!-- Alerts Summary -->
          <div class="bg-white rounded-xl shadow-sm p-6">
            <h3 class="text-lg font-semibold text-gray-800 mb-4">Alertes</h3>
            <div *ngIf="alertSummary" class="space-y-3">
              <div class="flex justify-between items-center">
                <span class="text-gray-500">Non lues</span>
                <span class="bg-blue-100 text-blue-700 px-2 py-1 rounded-full text-sm">
                  {{ alertSummary.newAlerts }}
                </span>
              </div>
              <div class="flex justify-between items-center">
                <span class="text-gray-500">Critiques</span>
                <span class="bg-red-100 text-red-700 px-2 py-1 rounded-full text-sm">
                  {{ alertSummary.criticalCount }}
                </span>
              </div>
              <a [routerLink]="['/alerts']" [queryParams]="{farmId: farm.id}" 
                 class="block text-center text-primary hover:underline text-sm mt-4">
                Voir toutes les alertes
              </a>
            </div>
            <div *ngIf="!alertSummary && !alertsLoading" class="text-center text-gray-500">
              Aucune alerte
            </div>
          </div>
        </div>

        <!-- Parcels Section -->
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex justify-between items-center mb-6">
            <h3 class="text-xl font-semibold text-gray-800">Parcelles</h3>
            <a [routerLink]="['/farms', farm.id, 'parcels', 'new']" 
               class="bg-primary text-white px-4 py-2 rounded-lg hover:bg-primary-dark text-sm">
              + Nouvelle Parcelle
            </a>
          </div>

          <!-- Empty Parcels -->
          <div *ngIf="!farm.parcels || farm.parcels.length === 0" class="text-center py-8 text-gray-500">
            Aucune parcelle pour cette ferme.
          </div>

          <!-- Parcels Grid -->
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <div *ngFor="let parcel of farm.parcels" 
                 class="border rounded-lg p-4 hover:border-primary transition-colors">
              <div class="flex justify-between items-start mb-2">
                <h4 class="font-medium text-gray-800">{{ parcel.name }}</h4>
                <span class="text-xs px-2 py-1 rounded-full"
                      [class.bg-green-100]="parcel.status === 'ACTIVE'"
                      [class.text-green-700]="parcel.status === 'ACTIVE'"
                      [class.bg-yellow-100]="parcel.status === 'PREPARATION'"
                      [class.text-yellow-700]="parcel.status === 'PREPARATION'"
                      [class.bg-gray-100]="parcel.status !== 'ACTIVE' && parcel.status !== 'PREPARATION'"
                      [class.text-gray-600]="parcel.status !== 'ACTIVE' && parcel.status !== 'PREPARATION'">
                  {{ parcel.status || 'Inconnu' }}
                </span>
              </div>
              <div class="text-sm text-gray-600 space-y-1">
                <p>Surface: {{ parcel.area }} ha</p>
                <p>Type de sol: {{ parcel.soilType }}</p>
                <p>Irrigation: {{ parcel.irrigationType }}</p>
                <p>
                  Humidité sol:
                  <span class="font-medium text-gray-800">
                    {{ parcel.currentMoisture !== undefined ? (parcel.currentMoisture + '%') : 'N/A' }}
                  </span>
                </p>
              </div>
              <div class="flex gap-2 mt-3 pt-3 border-t">
                <a [routerLink]="['/farms', farm.id, 'parcels', parcel.id]" 
                   class="text-primary text-sm hover:underline">
                  Détails
                </a>
                <a [routerLink]="['/irrigation']" [queryParams]="{parcelId: parcel.id}"
                   class="text-teal-600 text-sm hover:underline">
                  Irrigation
                </a>
                <button (click)="getRecommendation(parcel)"
                        class="text-indigo-600 text-sm hover:underline ml-auto flex items-center gap-1"
                        [disabled]="loadingRecommendation === parcel.id">
                  <span *ngIf="loadingRecommendation !== parcel.id">🤖 Obtenir conseil</span>
                  <span *ngIf="loadingRecommendation === parcel.id" class="text-gray-400">Analyse...</span>
                </button>
              </div>

              <!-- Recommendation Panel -->
              <div *ngIf="recommendation && recommendation.parcelId === parcel.id"
                   class="mt-3 rounded-lg border p-3 text-sm"
                   [class.border-green-300]="recommendation.shouldIrrigate"
                   [class.bg-green-50]="recommendation.shouldIrrigate"
                   [class.border-blue-300]="!recommendation.shouldIrrigate"
                   [class.bg-blue-50]="!recommendation.shouldIrrigate">
                
                <!-- Header -->
                <div class="flex justify-between items-center mb-2">
                  <div class="flex items-center gap-2 font-semibold"
                       [class.text-green-700]="recommendation.shouldIrrigate"
                       [class.text-blue-700]="!recommendation.shouldIrrigate">
                    <span>{{ recommendation.shouldIrrigate ? '💧 Irrigation recommandée' : '✅ Pas d\'irrigation nécessaire' }}</span>
                    <span class="text-xs font-normal px-2 py-0.5 rounded-full"
                          [class.bg-green-200]="recommendation.shouldIrrigate"
                          [class.bg-blue-200]="!recommendation.shouldIrrigate">
                      Confiance : {{ recommendation.confidenceScore }}%
                    </span>
                  </div>
                  <button (click)="recommendation = null" class="text-gray-400 hover:text-gray-600 text-xs">✕</button>
                </div>

                <!-- Key metrics -->
                <div class="grid grid-cols-2 gap-2 mb-2">
                  <div class="bg-white rounded p-2 text-center">
                    <p class="text-gray-500 text-xs">Durée recommandée</p>
                    <p class="font-bold text-gray-800">{{ recommendation.recommendedDurationMinutes }} min</p>
                  </div>
                  <div class="bg-white rounded p-2 text-center">
                    <p class="text-gray-500 text-xs">Quantité d'eau</p>
                    <p class="font-bold text-gray-800">{{ recommendation.recommendedWaterAmount | number:'1.0-0' }} L</p>
                  </div>
                </div>

                <!-- Conditions -->
                <div *ngIf="recommendation.conditions" class="grid grid-cols-3 gap-1 mb-2 text-xs text-gray-600">
                  <span *ngIf="recommendation.conditions.temperature !== undefined">🌡️ {{ recommendation.conditions.temperature }}°C</span>
                  <span *ngIf="recommendation.conditions.humidity !== undefined">💦 {{ recommendation.conditions.humidity }}%</span>
                  <span *ngIf="recommendation.conditions.soilMoisture !== undefined">🌱 Sol {{ recommendation.conditions.soilMoisture }}%</span>
                </div>

                <!-- Optimal time -->
                <div class="bg-white rounded p-2 mb-2 text-xs">
                  <span class="text-gray-500">⏰ Heure optimale : </span>
                  <span class="font-medium text-gray-800">{{ recommendation.optimalStartTime | date:'HH:mm' }}</span>
                </div>

                <!-- Reasons -->
                <div *ngIf="recommendation.reasons.length" class="mb-1">
                  <p class="text-gray-500 text-xs mb-1">Raisons :</p>
                  <ul class="space-y-1">
                    <li *ngFor="let r of recommendation.reasons" class="text-xs text-gray-700 flex gap-1">
                      <span>•</span><span>{{ r }}</span>
                    </li>
                  </ul>
                </div>

                <!-- Warnings -->
                <div *ngIf="recommendation.warnings.length">
                  <p class="text-amber-600 text-xs mb-1">⚠️ Avertissements :</p>
                  <ul class="space-y-1">
                    <li *ngFor="let w of recommendation.warnings" class="text-xs text-amber-700 flex gap-1">
                      <span>•</span><span>{{ w }}</span>
                    </li>
                  </ul>
                </div>

                <!-- Error message -->
                <div *ngIf="recommendationError && recommendation.parcelId === parcel.id"
                     class="text-red-600 text-xs mt-1">
                  {{ recommendationError }}
                </div>
              </div>

              <!-- Recommendation error (no result) -->
              <div *ngIf="recommendationError && loadingRecommendation !== parcel.id && (!recommendation || recommendation.parcelId !== parcel.id)"
                   class="mt-2 bg-red-50 text-red-600 rounded p-2 text-xs">
                {{ recommendationError }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class FarmDetailComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private farmService = inject(FarmService);
  private weatherService = inject(WeatherService);
  private alertService = inject(AlertService);
  private irrigationService = inject(IrrigationService);

  farm: Farm | null = null;
  weather: CurrentWeather | null = null;
  alertSummary: AlertSummary | null = null;
  recommendation: IrrigationRecommendation | null = null;

  loading = true;
  weatherLoading = false;
  alertsLoading = false;
  loadingRecommendation: number | null = null;
  error = '';
  recommendationError = '';
  private parcelsRefreshTimer: ReturnType<typeof setInterval> | null = null;

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('farmId') || this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadFarm(+id);
    }
  }

  ngOnDestroy() {
    if (this.parcelsRefreshTimer) {
      clearInterval(this.parcelsRefreshTimer);
      this.parcelsRefreshTimer = null;
    }
  }

  loadFarm(id: number) {
    this.farmService.getFarmById(id).subscribe({
      next: (farm) => {
        this.farm = farm;
        this.loading = false;
        this.loadParcels(id);
        this.startParcelsRefresh(id);
        this.loadWeather();
        this.loadAlerts();
      },
      error: (err) => {
        this.error = 'Ferme non trouvée';
        this.loading = false;
        console.error(err);
      }
    });
  }

  private startParcelsRefresh(farmId: number) {
    if (this.parcelsRefreshTimer) {
      clearInterval(this.parcelsRefreshTimer);
    }

    this.parcelsRefreshTimer = setInterval(() => {
      this.loadParcels(farmId);
    }, 15000);
  }

  loadParcels(farmId: number) {
    this.farmService.getParcelsByFarm(farmId).subscribe({
      next: (parcels) => {
        if (this.farm) {
          this.farm.parcels = parcels;
        }
      },
      error: (err) => {
        console.error('Error loading parcels:', err);
      }
    });
  }

  loadWeather() {
    if (this.farm?.latitude && this.farm?.longitude) {
      this.weatherLoading = true;
      this.weatherService.getCurrentWeather(this.farm.latitude, this.farm.longitude).subscribe({
        next: (weather) => {
          this.weather = weather;
          this.weatherLoading = false;
        },
        error: () => {
          this.weatherLoading = false;
        }
      });
    }
  }

  loadAlerts() {
    if (this.farm?.id) {
      this.alertsLoading = true;
      this.alertService.getAlertSummary(this.farm.id).subscribe({
        next: (summary) => {
          this.alertSummary = summary;
          this.alertsLoading = false;
        },
        error: () => {
          this.alertsLoading = false;
        }
      });
    }
  }

  deleteFarm() {
    if (!this.farm) return;
    
    if (confirm('Êtes-vous sûr de vouloir supprimer cette ferme ?')) {
      this.farmService.deleteFarm(this.farm.id).subscribe({
        next: () => {
          this.router.navigate(['/farms']);
        },
        error: (err) => {
          this.error = 'Erreur lors de la suppression';
          console.error(err);
        }
      });
    }
  }

  getRecommendation(parcel: Parcel) {
    if (!this.farm) return;

    const lat = parcel.latitude ?? this.farm.latitude;
    const lon = parcel.longitude ?? this.farm.longitude;
    const soilMoisture = this.estimateSoilMoisture(parcel);

    if (!lat || !lon) {
      this.recommendationError = 'Coordonnées GPS manquantes pour cette parcelle.';
      this.recommendation = null;
      return;
    }

    this.loadingRecommendation = parcel.id;
    this.recommendation = null;
    this.recommendationError = '';

    this.irrigationService.getRecommendation(parcel.id, this.farm.id, lat, lon, soilMoisture).subscribe({
      next: (rec) => {
        this.recommendation = rec;
        this.loadingRecommendation = null;
      },
      error: (err) => {
        this.recommendationError = 'Impossible d\'obtenir le conseil. Vérifiez que le service d\'irrigation est disponible.';
        this.loadingRecommendation = null;
        console.error(err);
      }
    });
  }

  private estimateSoilMoisture(parcel: Parcel): number | undefined {
    if (typeof parcel.currentMoisture === 'number') {
      return parcel.currentMoisture;
    }

    const min = parcel.optimalMoistureMin;
    const max = parcel.optimalMoistureMax;

    if (typeof min === 'number' && typeof max === 'number') {
      return Math.round(((min + max) / 2) * 10) / 10;
    }

    if (typeof min === 'number') {
      return min;
    }

    if (typeof max === 'number') {
      return max;
    }

    const soilTypeDefaults: Record<string, number> = {
      CLAY: 55,
      LOAMY: 45,
      SILTY: 50,
      SANDY: 30
    };

    return soilTypeDefaults[parcel.soilType?.toUpperCase() ?? ''];
  }
}
