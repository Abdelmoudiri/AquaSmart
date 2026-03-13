import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { IrrigationService } from '../../core/services/irrigation.service';
import { FarmService } from '../../core/services/farm.service';
import { IrrigationSchedule, IrrigationEvent, IrrigationRecommendation } from '../../core/models/irrigation.model';
import { Farm, Parcel } from '../../core/models/farm.model';
import { AuthService } from '../../core/services/auth.service';
import { NavbarComponent } from '../../core/components/navbar/navbar.component';

@Component({
  selector: 'app-irrigation',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, NavbarComponent],
  template: `
    <app-navbar></app-navbar>
    <div class="p-6">
      <!-- Header -->
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold text-gray-800">Irrigation</h1>
      </div>

      <!-- Farm Selection -->
      <div class="bg-white rounded-xl shadow-sm p-6 mb-6">
        <h2 class="text-lg font-semibold text-gray-800 mb-4">Sélectionner une ferme</h2>
        <select [(ngModel)]="selectedFarmId" 
                (change)="onFarmChange()"
                class="w-full md:w-1/3 px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary">
          <option [value]="null">-- Choisir une ferme --</option>
          <option *ngFor="let farm of farms" [value]="farm.id">{{ farm.name }}</option>
        </select>
      </div>

      <div *ngIf="selectedFarm" class="space-y-6">
        
        <!-- Quick Actions -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          
          <!-- Manual Irrigation Card -->
          <div class="bg-white rounded-xl shadow-sm p-6">
            <h3 class="text-lg font-semibold text-gray-800 mb-4">Irrigation Manuelle</h3>
            <div class="space-y-4">
              <select [(ngModel)]="manualParcelId" 
                      class="w-full px-3 py-2 border rounded-lg text-sm">
                <option [value]="null">-- Parcelle --</option>
                <option *ngFor="let parcel of selectedFarm.parcels" [value]="parcel.id">
                  {{ parcel.name }}
                </option>
              </select>
              <div class="grid grid-cols-2 gap-2">
                <input type="number" 
                       [(ngModel)]="manualDuration" 
                       placeholder="Durée (min)"
                       class="px-3 py-2 border rounded-lg text-sm">
                <input type="number" 
                       [(ngModel)]="manualWaterAmount" 
                       placeholder="Eau (L)"
                       class="px-3 py-2 border rounded-lg text-sm">
              </div>
              <button (click)="triggerManualIrrigation()"
                      [disabled]="!manualParcelId"
                      class="w-full bg-teal-500 text-white py-2 rounded-lg hover:bg-teal-600 disabled:opacity-50">
                Démarrer
              </button>
            </div>
          </div>

          <!-- In Progress -->
          <div class="bg-white rounded-xl shadow-sm p-6">
            <h3 class="text-lg font-semibold text-gray-800 mb-4">En cours</h3>
            <div *ngIf="inProgressEvents.length === 0" class="text-gray-500 text-sm">
              Aucune irrigation en cours
            </div>
            <div *ngFor="let event of inProgressEvents" class="border-b last:border-0 py-2">
              <div class="flex justify-between items-center">
                <span class="text-sm font-medium">Parcelle #{{ event.parcelId }}</span>
                <span class="text-xs bg-green-100 text-green-700 px-2 py-1 rounded-full">
                  En cours
                </span>
              </div>
              <div class="text-xs text-gray-500 mt-1">
                {{ event.waterAmount }}L - {{ event.durationMinutes }}min
              </div>
              <button (click)="cancelEvent(event)" 
                      class="text-xs text-red-500 hover:underline mt-1">
                Annuler
              </button>
            </div>
          </div>

          <!-- Recommendation -->
          <div class="bg-white rounded-xl shadow-sm p-6">
            <h3 class="text-lg font-semibold text-gray-800 mb-4">Recommandation IA</h3>
            <div class="space-y-4">
              <select [(ngModel)]="recommendationParcelId" 
                      class="w-full px-3 py-2 border rounded-lg text-sm">
                <option [value]="null">-- Parcelle --</option>
                <option *ngFor="let parcel of selectedFarm.parcels" [value]="parcel.id">
                  {{ parcel.name }}
                </option>
              </select>
              <button (click)="getRecommendation()"
                      [disabled]="!recommendationParcelId"
                      class="w-full bg-primary text-white py-2 rounded-lg hover:bg-primary-dark disabled:opacity-50">
                Obtenir conseil
              </button>
              
              <div *ngIf="recommendation" class="mt-4 p-4 bg-blue-50 rounded-lg">
                <div class="flex items-center gap-2 mb-2">
                  <span class="text-2xl">{{ hasInsufficientData() ? '⚠️' : (recommendation.shouldIrrigate ? '💧' : '☀️') }}</span>
                  <span class="font-medium" 
                        [class.text-blue-700]="recommendation.shouldIrrigate && !hasInsufficientData()"
                        [class.text-orange-600]="!recommendation.shouldIrrigate && !hasInsufficientData()"
                        [class.text-amber-700]="hasInsufficientData()">
                    {{ hasInsufficientData() ? 'Données insuffisantes' : (recommendation.shouldIrrigate ? 'Irrigation recommandée' : 'Pas nécessaire') }}
                  </span>
                </div>
                <p *ngIf="recommendation.reasons.length" class="text-sm text-gray-600">{{ recommendation.reasons[0] }}</p>
                <div *ngIf="recommendation.warnings.length" class="mt-2 text-xs text-amber-700">
                  {{ recommendation.warnings[0] }}
                </div>
                <div *ngIf="recommendation.shouldIrrigate" class="mt-2 text-sm">
                  <p><strong>Eau:</strong> {{ recommendation.recommendedWaterAmount }}L</p>
                  <p><strong>Durée:</strong> {{ recommendation.recommendedDurationMinutes }}min</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Schedules Section -->
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex justify-between items-center mb-6">
            <h3 class="text-xl font-semibold text-gray-800">Plannings d'irrigation</h3>
            <button (click)="showScheduleForm = !showScheduleForm"
                    class="bg-primary text-white px-4 py-2 rounded-lg text-sm hover:bg-primary-dark">
              {{ showScheduleForm ? 'Annuler' : '+ Nouveau planning' }}
            </button>
          </div>

          <!-- New Schedule Form -->
          <div *ngIf="showScheduleForm" class="bg-gray-50 p-4 rounded-lg mb-6">
            <h4 class="font-medium mb-4">Nouveau planning</h4>
            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
              <select [(ngModel)]="newSchedule.parcelId" class="px-3 py-2 border rounded-lg">
                <option [value]="null">-- Parcelle --</option>
                <option *ngFor="let parcel of selectedFarm.parcels" [value]="parcel.id">
                  {{ parcel.name }}
                </option>
              </select>
              <input type="time" [(ngModel)]="newSchedule.startTime" 
                     class="px-3 py-2 border rounded-lg" placeholder="Heure de début">
              <input type="number" [(ngModel)]="newSchedule.durationMinutes" 
                     class="px-3 py-2 border rounded-lg" placeholder="Durée (min)">
              <input type="number" [(ngModel)]="newSchedule.waterAmount" 
                     class="px-3 py-2 border rounded-lg" placeholder="Eau (L)">
              <select [(ngModel)]="newSchedule.frequency" class="px-3 py-2 border rounded-lg">
                <option value="DAILY">Quotidien</option>
                <option value="EVERY_OTHER_DAY">Tous les 2 jours</option>
                <option value="WEEKLY">Hebdomadaire</option>
              </select>
              <button (click)="createSchedule()" 
                      class="bg-teal-500 text-white rounded-lg hover:bg-teal-600">
                Créer
              </button>
            </div>
          </div>

          <!-- Schedules List -->
          <div *ngIf="schedules.length === 0 && !showScheduleForm" class="text-center py-8 text-gray-500">
            Aucun planning configuré
          </div>
          
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <div *ngFor="let schedule of schedules" 
                 class="border rounded-lg p-4"
                 [class.border-green-300]="schedule.active"
                 [class.bg-green-50]="schedule.active">
              <div class="flex justify-between items-start mb-2">
                <div>
                  <h4 class="font-medium">Parcelle #{{ schedule.parcelId }}</h4>
                  <p class="text-sm text-gray-500">{{ schedule.frequency }}</p>
                </div>
                <span class="text-xs px-2 py-1 rounded-full"
                      [class.bg-green-100]="schedule.active"
                      [class.text-green-700]="schedule.active"
                      [class.bg-gray-100]="!schedule.active"
                      [class.text-gray-600]="!schedule.active">
                  {{ schedule.active ? 'Actif' : 'Inactif' }}
                </span>
              </div>
              <div class="text-sm text-gray-600 space-y-1">
                <p>⏰ {{ schedule.startTime }}</p>
                <p>⏱️ {{ schedule.durationMinutes }} min</p>
                <p>💧 {{ schedule.waterAmount }}L</p>
              </div>
              <div class="flex gap-2 mt-3 pt-3 border-t">
                <button (click)="toggleSchedule(schedule)" 
                        class="text-xs hover:underline"
                        [class.text-red-500]="schedule.active"
                        [class.text-green-600]="!schedule.active">
                  {{ schedule.active ? 'Désactiver' : 'Activer' }}
                </button>
                <button (click)="deleteSchedule(schedule)" 
                        class="text-xs text-red-500 hover:underline">
                  Supprimer
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class IrrigationComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private irrigationService = inject(IrrigationService);
  private farmService = inject(FarmService);
  private authService = inject(AuthService);

  farms: Farm[] = [];
  selectedFarmId: number | null = null;
  selectedFarm: Farm | null = null;
  
  schedules: IrrigationSchedule[] = [];
  inProgressEvents: IrrigationEvent[] = [];
  recommendation: IrrigationRecommendation | null = null;

  // Manual irrigation
  manualParcelId: number | null = null;
  manualDuration = 30;
  manualWaterAmount = 100;

  // Recommendation
  recommendationParcelId: number | null = null;

  // New schedule form
  showScheduleForm = false;
  newSchedule = {
    parcelId: null as number | null,
    startTime: '06:00',
    durationMinutes: 30,
    waterAmount: 100,
    frequency: 'DAILY'
  };
  private parcelsRefreshTimer: ReturnType<typeof setInterval> | null = null;

  ngOnInit() {
    this.loadFarms();
    
    // Check if parcelId is in query params
    this.route.queryParams.subscribe(params => {
      if (params['parcelId']) {
        // TODO: Load farm for this parcel
      }
    });
  }

  ngOnDestroy() {
    if (this.parcelsRefreshTimer) {
      clearInterval(this.parcelsRefreshTimer);
      this.parcelsRefreshTimer = null;
    }
  }

  loadFarms() {
    const user = this.authService.currentUser();
    if (!user) return;

    // Utiliser ownerId par défaut (1) pour le développement
    this.farmService.getFarmsByOwner(1).subscribe({
      next: (farms) => {
        this.farms = farms;
        if (farms.length === 1) {
          this.selectedFarmId = farms[0].id;
          this.onFarmChange();
        }
      }
    });
  }

  onFarmChange() {
    if (!this.selectedFarmId) {
      this.selectedFarm = null;
      return;
    }

    this.farmService.getFarmById(this.selectedFarmId).subscribe({
      next: (farm) => {
        this.selectedFarm = farm;
        // Charger les parcelles séparément car l'API ne les inclut pas
        this.loadParcels(this.selectedFarmId!);
        this.startParcelsRefresh(this.selectedFarmId!);
        this.loadSchedules();
        this.loadInProgressEvents();
      }
    });
  }

  loadParcels(farmId: number) {
    this.farmService.getParcelsByFarm(farmId).subscribe({
      next: (parcels) => {
        if (this.selectedFarm) {
          this.selectedFarm.parcels = parcels;
        }
      },
      error: (err) => {
        console.error('Error loading parcels:', err);
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

  loadSchedules() {
    if (!this.selectedFarmId) return;

    this.irrigationService.getSchedulesByFarm(this.selectedFarmId).subscribe({
      next: (schedules) => {
        this.schedules = schedules;
      }
    });
  }

  loadInProgressEvents() {
    this.irrigationService.getInProgressEvents().subscribe({
      next: (events) => {
        this.inProgressEvents = events.filter(e => e.farmId === this.selectedFarmId);
      }
    });
  }

  triggerManualIrrigation() {
    if (!this.manualParcelId || !this.selectedFarmId) return;

    this.irrigationService.triggerManualIrrigation(
      this.manualParcelId,
      this.selectedFarmId,
      this.manualDuration,
      this.manualWaterAmount
    ).subscribe({
      next: (event) => {
        this.inProgressEvents.push(event);
        this.manualParcelId = null;
      },
      error: (err) => {
        alert('Erreur lors du démarrage de l\'irrigation');
        console.error(err);
      }
    });
  }

  cancelEvent(event: IrrigationEvent) {
    this.irrigationService.cancelEvent(event.id, 'Annulé manuellement').subscribe({
      next: () => {
        this.inProgressEvents = this.inProgressEvents.filter(e => e.id !== event.id);
      }
    });
  }

  getRecommendation() {
    if (!this.recommendationParcelId || !this.selectedFarm) return;

    const parcel = this.selectedFarm.parcels?.find(p => p.id === this.recommendationParcelId);
    const lat = parcel?.latitude ?? this.selectedFarm.latitude ?? 31.63;
    const lon = parcel?.longitude ?? this.selectedFarm.longitude ?? -8.0;
    const soilMoisture = parcel ? this.estimateSoilMoisture(parcel) : undefined;

    this.irrigationService.getRecommendation(
      this.recommendationParcelId,
      this.selectedFarm.id,
      lat,
      lon,
      soilMoisture
    ).subscribe({
      next: (rec) => {
        this.recommendation = rec;
      },
      error: (err) => {
        alert('Erreur lors de la récupération de la recommandation');
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

  hasInsufficientData(): boolean {
    return !!this.recommendation?.warnings?.some(w =>
      w.includes('Weather data unavailable') || w.includes('No soil moisture data available')
    );
  }

  createSchedule() {
    if (!this.newSchedule.parcelId || !this.selectedFarmId) return;

    const schedule = {
      ...this.newSchedule,
      farmId: this.selectedFarmId
    };

    this.irrigationService.createSchedule(schedule).subscribe({
      next: (created) => {
        this.schedules.push(created);
        this.showScheduleForm = false;
        this.newSchedule = {
          parcelId: null,
          startTime: '06:00',
          durationMinutes: 30,
          waterAmount: 100,
          frequency: 'DAILY'
        };
      },
      error: (err) => {
        alert('Erreur lors de la création du planning');
        console.error(err);
      }
    });
  }

  toggleSchedule(schedule: IrrigationSchedule) {
    const action = schedule.active 
      ? this.irrigationService.deactivateSchedule(schedule.id)
      : this.irrigationService.activateSchedule(schedule.id);

    action.subscribe({
      next: (updated) => {
        const index = this.schedules.findIndex(s => s.id === schedule.id);
        if (index !== -1) {
          this.schedules[index] = updated;
        }
      }
    });
  }

  deleteSchedule(schedule: IrrigationSchedule) {
    if (confirm('Supprimer ce planning ?')) {
      this.irrigationService.deleteSchedule(schedule.id).subscribe({
        next: () => {
          this.schedules = this.schedules.filter(s => s.id !== schedule.id);
        }
      });
    }
  }
}
