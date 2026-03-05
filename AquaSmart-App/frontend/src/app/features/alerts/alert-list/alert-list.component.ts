import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { AlertService } from '../../../core/services/alert.service';
import { Alert } from '../../../core/models/alert.model';
import { NavbarComponent } from '../../../core/components/navbar/navbar.component';

@Component({
  selector: 'app-alert-list',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent],
  template: `
    <app-navbar></app-navbar>
    <div class="p-6">
      <!-- Header -->
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold text-gray-800">Alertes</h1>
        <button *ngIf="alerts.length > 0" 
                (click)="markAllAsRead()"
                class="text-primary hover:underline text-sm">
          Tout marquer comme lu
        </button>
      </div>

      <!-- Loading -->
      <div *ngIf="loading" class="text-center py-8">
        <p class="text-gray-500">Chargement...</p>
      </div>

      <!-- Error -->
      <div *ngIf="error" class="bg-red-100 text-red-700 p-4 rounded-lg mb-4">
        {{ error }}
      </div>

      <!-- Filters -->
      <div class="flex gap-2 mb-6">
        <button (click)="filterBy('all')" 
                [class.bg-primary]="filter === 'all'"
                [class.text-white]="filter === 'all'"
                [class.bg-gray-100]="filter !== 'all'"
                class="px-4 py-2 rounded-lg text-sm transition-colors">
          Toutes
        </button>
        <button (click)="filterBy('unread')" 
                [class.bg-primary]="filter === 'unread'"
                [class.text-white]="filter === 'unread'"
                [class.bg-gray-100]="filter !== 'unread'"
                class="px-4 py-2 rounded-lg text-sm transition-colors">
          Non lues
        </button>
        <button (click)="filterBy('critical')" 
                [class.bg-red-500]="filter === 'critical'"
                [class.text-white]="filter === 'critical'"
                [class.bg-gray-100]="filter !== 'critical'"
                class="px-4 py-2 rounded-lg text-sm transition-colors">
          Critiques
        </button>
      </div>

      <!-- Empty State -->
      <div *ngIf="!loading && filteredAlerts.length === 0" 
           class="text-center py-12 bg-white rounded-xl shadow-sm">
        <p class="text-gray-500">Aucune alerte</p>
      </div>

      <!-- Alerts List -->
      <div class="space-y-4">
        <div *ngFor="let alert of filteredAlerts" 
             class="bg-white rounded-xl shadow-sm p-4 border-l-4 transition-all hover:shadow-md"
             [class.border-red-500]="alert.severity === 'CRITICAL' || alert.severity === 'EMERGENCY'"
             [class.border-orange-500]="alert.severity === 'WARNING'"
             [class.border-blue-500]="alert.severity === 'INFO'"
             [class.opacity-60]="alert.status === 'READ'">
          
          <div class="flex justify-between items-start">
            <!-- Alert Content -->
            <div class="flex-1">
              <div class="flex items-center gap-2 mb-2">
                <!-- Severity Badge -->
                <span class="text-xs px-2 py-1 rounded-full font-medium"
                      [class.bg-red-100]="alert.severity === 'CRITICAL' || alert.severity === 'EMERGENCY'"
                      [class.text-red-700]="alert.severity === 'CRITICAL' || alert.severity === 'EMERGENCY'"
                      [class.bg-orange-100]="alert.severity === 'WARNING'"
                      [class.text-orange-700]="alert.severity === 'WARNING'"
                      [class.bg-blue-100]="alert.severity === 'INFO'"
                      [class.text-blue-700]="alert.severity === 'INFO'">
                  {{ getSeverityLabel(alert.severity) }}
                </span>
                <!-- Type Badge -->
                <span class="text-xs px-2 py-1 rounded-full bg-gray-100 text-gray-600">
                  {{ getTypeLabel(alert.type) }}
                </span>
                <!-- Unread Indicator -->
                <span *ngIf="alert.status === 'NEW'" class="w-2 h-2 bg-blue-500 rounded-full"></span>
              </div>
              
              <h3 class="font-semibold text-gray-800">{{ alert.title }}</h3>
              <p class="text-gray-600 text-sm mt-1">{{ alert.message }}</p>
              
              <div class="flex items-center gap-4 mt-3 text-xs text-gray-500">
                <span>{{ formatDate(alert.createdAt) }}</span>
                <span *ngIf="alert.parcelId">Parcelle #{{ alert.parcelId }}</span>
              </div>
            </div>

            <!-- Actions -->
            <div class="flex flex-col gap-2 ml-4">
              <button *ngIf="alert.status === 'NEW'" 
                      (click)="markAsRead(alert)"
                      class="text-xs text-primary hover:underline">
                Marquer lu
              </button>
              <button *ngIf="alert.status !== 'RESOLVED'" 
                      (click)="resolveAlert(alert)"
                      class="text-xs text-green-600 hover:underline">
                Résoudre
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class AlertListComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private alertService = inject(AlertService);

  alerts: Alert[] = [];
  filteredAlerts: Alert[] = [];
  filter: 'all' | 'unread' | 'critical' = 'all';
  farmId: number | null = null;
  loading = true;
  error = '';

  ngOnInit() {
    // Get farmId from query params if provided
    this.route.queryParams.subscribe(params => {
      if (params['farmId']) {
        this.farmId = +params['farmId'];
        this.loadAlerts();
      } else {
        this.loading = false;
        this.error = 'Veuillez sélectionner une ferme';
      }
    });
  }

  loadAlerts() {
    if (!this.farmId) return;

    this.alertService.getAlertsByFarm(this.farmId).subscribe({
      next: (response) => {
        this.alerts = response.content || response;
        this.applyFilter();
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des alertes';
        this.loading = false;
        console.error(err);
      }
    });
  }

  filterBy(filter: 'all' | 'unread' | 'critical') {
    this.filter = filter;
    this.applyFilter();
  }

  applyFilter() {
    switch (this.filter) {
      case 'unread':
        this.filteredAlerts = this.alerts.filter(a => a.status === 'NEW');
        break;
      case 'critical':
        this.filteredAlerts = this.alerts.filter(a => a.severity === 'CRITICAL' || a.severity === 'EMERGENCY');
        break;
      default:
        this.filteredAlerts = [...this.alerts];
    }
  }

  markAsRead(alert: Alert) {
    this.alertService.markAsRead(alert.id).subscribe({
      next: (updated) => {
        const index = this.alerts.findIndex(a => a.id === alert.id);
        if (index !== -1) {
          this.alerts[index] = updated;
          this.applyFilter();
        }
      }
    });
  }

  markAllAsRead() {
    if (!this.farmId) return;

    this.alertService.markAllAsRead(this.farmId).subscribe({
      next: () => {
        this.alerts.forEach(a => a.status = 'READ');
        this.applyFilter();
      }
    });
  }

  resolveAlert(alert: Alert) {
    const resolution = prompt('Raison de la résolution:');
    if (resolution) {
      this.alertService.resolveAlert(alert.id, resolution).subscribe({
        next: (updated) => {
          const index = this.alerts.findIndex(a => a.id === alert.id);
          if (index !== -1) {
            this.alerts[index] = updated;
            this.applyFilter();
          }
        }
      });
    }
  }

  getSeverityLabel(severity: string): string {
    const labels: Record<string, string> = {
      'CRITICAL': 'Critique',
      'EMERGENCY': 'Urgence',
      'WARNING': 'Attention',
      'INFO': 'Info'
    };
    return labels[severity] || severity;
  }

  getTypeLabel(type: string): string {
    const labels: Record<string, string> = {
      'LOW_SOIL_MOISTURE': 'Sol sec',
      'HIGH_SOIL_MOISTURE': 'Sol humide',
      'FROST_WARNING': 'Gel',
      'HEAT_WAVE': 'Canicule',
      'IRRIGATION_FAILED': 'Irrigation échouée',
      'SYSTEM_ERROR': 'Erreur système'
    };
    return labels[type] || type;
  }

  formatDate(date: string): string {
    return new Date(date).toLocaleString('fr-FR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }
}
