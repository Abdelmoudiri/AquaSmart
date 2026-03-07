import { Component, OnInit, inject, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { FarmService } from '../../core/services/farm.service';
import { AlertService } from '../../core/services/alert.service';
import { IrrigationService } from '../../core/services/irrigation.service';
import { Router } from '@angular/router';
import { Farm } from '../../core/models/farm.model';
import { AlertSummary } from '../../core/models/alert.model';
import { Chart, registerables } from 'chart.js';
import { NavbarComponent } from '../../core/components/navbar/navbar.component';

// Register Chart.js
Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent],
  template: `
    <div class="min-h-screen bg-gray-50">
      <!-- Navigation -->
      <app-navbar></app-navbar>

      <!-- Content -->
      <div class="max-w-7xl mx-auto px-4 py-8">
        
        <!-- Welcome -->
        <div class="mb-8">
          <h1 class="text-3xl font-bold text-gray-800">
            Bonjour{{ (user$ | async)?.firstName ? ', ' + (user$ | async)?.firstName : '' }} 👋
          </h1>
          <p class="text-gray-500 mt-1">Bienvenue sur votre tableau de bord AquaSmart</p>
        </div>

        <!-- Stats Cards -->
        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          <div class="bg-white rounded-xl shadow-sm p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-gray-500 text-sm">Fermes</p>
                <p class="text-3xl font-bold text-gray-800">{{ farms.length }}</p>
              </div>
              <div class="text-4xl">🌾</div>
            </div>
          </div>
          
          <div class="bg-white rounded-xl shadow-sm p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-gray-500 text-sm">Parcelles</p>
                <p class="text-3xl font-bold text-gray-800">{{ totalParcels }}</p>
              </div>
              <div class="text-4xl">📐</div>
            </div>
          </div>
          
          <div class="bg-white rounded-xl shadow-sm p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-gray-500 text-sm">Surface totale</p>
                <p class="text-3xl font-bold text-gray-800">{{ totalArea | number:'1.0-1' }} ha</p>
              </div>
              <div class="text-4xl">📏</div>
            </div>
          </div>
          
          <div class="bg-white rounded-xl shadow-sm p-6">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-gray-500 text-sm">Alertes actives</p>
                <p class="text-3xl font-bold" 
                   [class.text-red-600]="totalUnreadAlerts > 0"
                   [class.text-green-600]="totalUnreadAlerts === 0">
                  {{ totalUnreadAlerts }}
                </p>
              </div>
              <div class="text-4xl">{{ totalUnreadAlerts > 0 ? '🔔' : '✅' }}</div>
            </div>
          </div>
        </div>

        <!-- Quick Actions -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <a routerLink="/farms/new" 
             class="bg-primary text-white rounded-xl p-6 hover:bg-primary-dark transition-colors">
            <h3 class="text-xl font-semibold mb-2">+ Nouvelle Ferme</h3>
            <p class="text-primary-100 text-sm opacity-80">Ajouter une nouvelle exploitation</p>
          </a>
          
          <a routerLink="/irrigation" 
             class="bg-teal-500 text-white rounded-xl p-6 hover:bg-teal-600 transition-colors">
            <h3 class="text-xl font-semibold mb-2">💧 Irrigation</h3>
            <p class="text-teal-100 text-sm opacity-80">Gérer les plannings d'arrosage</p>
          </a>
          
          <a routerLink="/weather" 
             class="bg-blue-500 text-white rounded-xl p-6 hover:bg-blue-600 transition-colors">
            <h3 class="text-xl font-semibold mb-2">🌤️ Météo</h3>
            <p class="text-blue-100 text-sm opacity-80">Consulter les prévisions</p>
          </a>
        </div>

        <!-- Farms List -->
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex justify-between items-center mb-6">
            <h2 class="text-xl font-semibold text-gray-800">Mes Fermes</h2>
            <a routerLink="/farms" class="text-primary hover:underline text-sm">Voir tout →</a>
          </div>
          
          <!-- Loading -->
          <div *ngIf="loading" class="text-center py-8 text-gray-500">
            Chargement...
          </div>
          
          <!-- Empty State -->
          <div *ngIf="!loading && farms.length === 0" class="text-center py-8">
            <p class="text-gray-500 mb-4">Vous n'avez pas encore de ferme</p>
            <a routerLink="/farms/new" class="text-primary hover:underline">
              Créer votre première ferme
            </a>
          </div>
          
          <!-- Farms Grid -->
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <a *ngFor="let farm of farms.slice(0, 6)" 
               [routerLink]="['/farms', farm.id]"
               class="border rounded-lg p-4 hover:border-primary hover:shadow-md transition-all">
              <div class="flex justify-between items-start mb-2">
                <h3 class="font-semibold text-gray-800">{{ farm.name }}</h3>
                <span class="text-xs px-2 py-1 rounded-full"
                      [class.bg-green-100]="farm.active"
                      [class.text-green-700]="farm.active"
                      [class.bg-gray-100]="!farm.active"
                      [class.text-gray-600]="!farm.active">
                  {{ farm.active ? 'Active' : 'Inactive' }}
                </span>
              </div>
              <p class="text-sm text-gray-500">{{ farm.location }}</p>
              <div class="flex gap-4 mt-3 text-sm text-gray-600">
                <span>{{ farm.totalArea }} ha</span>
                <span>{{ farm.parcels?.length || 0 }} parcelles</span>
              </div>
            </a>
          </div>
        </div>

        <!-- Charts Section -->
        <div *ngIf="farms.length > 0" class="grid grid-cols-1 lg:grid-cols-2 gap-6 mt-8">
          
          <!-- Water Usage Chart (Bar) -->
          <div class="bg-white rounded-xl shadow-sm p-6">
            <div class="flex justify-between items-center mb-4">
              <h3 class="text-lg font-semibold text-gray-800">Consommation d'eau (7 jours)</h3>
              <a routerLink="/stats" class="text-primary text-sm hover:underline">Voir plus →</a>
            </div>
            <div class="h-64">
              <canvas #waterUsageChart></canvas>
            </div>
            <div class="flex justify-center gap-6 mt-4">
              <div class="flex items-center gap-2">
                <div class="w-3 h-3 rounded-full bg-blue-500"></div>
                <span class="text-sm text-gray-600">Eau utilisée</span>
              </div>
              <div class="flex items-center gap-2">
                <div class="w-3 h-3 rounded-full bg-green-500"></div>
                <span class="text-sm text-gray-600">Eau économisée</span>
              </div>
            </div>
          </div>

          <!-- Water Distribution Chart (Doughnut) -->
          <div class="bg-white rounded-xl shadow-sm p-6">
            <div class="flex justify-between items-center mb-4">
              <h3 class="text-lg font-semibold text-gray-800">Répartition par parcelle</h3>
            </div>
            <div class="h-64 flex items-center justify-center">
              <canvas #distributionChart></canvas>
            </div>
          </div>
        </div>

        <!-- Monthly Trend Chart -->
        <div *ngIf="farms.length > 0" class="bg-white rounded-xl shadow-sm p-6 mt-6">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-semibold text-gray-800">Évolution mensuelle</h3>
            <div class="flex gap-2">
              <button 
                (click)="toggleChartType()"
                class="px-3 py-1 bg-gray-100 text-gray-600 rounded-lg text-sm hover:bg-gray-200">
                {{ monthlyChartType === 'line' ? 'Barres' : 'Courbe' }}
              </button>
            </div>
          </div>
          <div class="h-72">
            <canvas #monthlyChart></canvas>
          </div>
        </div>

        <!-- Water Stats Summary -->
        <div *ngIf="farms.length > 0" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mt-8">
          <div class="bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl shadow-sm p-6 text-white">
            <p class="text-blue-100 text-sm">Eau utilisée (semaine)</p>
            <p class="text-3xl font-bold mt-1">{{ dashboardStats.totalWaterUsed | number:'1.0-0' }} L</p>
            <p class="text-blue-200 text-xs mt-2">
              <span [class.text-green-300]="dashboardStats.trend < 0" [class.text-red-300]="dashboardStats.trend > 0">
                {{ dashboardStats.trend > 0 ? '+' : '' }}{{ dashboardStats.trend }}%
              </span>
              vs semaine précédente
            </p>
          </div>
          
          <div class="bg-gradient-to-br from-green-500 to-green-600 rounded-xl shadow-sm p-6 text-white">
            <p class="text-green-100 text-sm">Eau économisée</p>
            <p class="text-3xl font-bold mt-1">{{ dashboardStats.totalWaterSaved | number:'1.0-0' }} L</p>
            <p class="text-green-200 text-xs mt-2">{{ dashboardStats.savingsPercentage }}% d'économie</p>
          </div>
          
          <div class="bg-gradient-to-br from-purple-500 to-purple-600 rounded-xl shadow-sm p-6 text-white">
            <p class="text-purple-100 text-sm">Irrigations</p>
            <p class="text-3xl font-bold mt-1">{{ dashboardStats.totalEvents }}</p>
            <p class="text-purple-200 text-xs mt-2">Cette semaine</p>
          </div>
          
          <div class="bg-gradient-to-br from-amber-500 to-amber-600 rounded-xl shadow-sm p-6 text-white">
            <p class="text-amber-100 text-sm">Efficacité</p>
            <p class="text-3xl font-bold mt-1">{{ 72 + (dashboardStats.savingsPercentage / 3) | number:'1.0-0' }}%</p>
            <p class="text-amber-200 text-xs mt-2">Score global</p>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class DashboardComponent implements OnInit, AfterViewInit {
  @ViewChild('waterUsageChart') waterUsageChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('distributionChart') distributionChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('monthlyChart') monthlyChartRef!: ElementRef<HTMLCanvasElement>;

  authService = inject(AuthService);
  farmService = inject(FarmService);
  alertService = inject(AlertService);
  irrigationService = inject(IrrigationService);
  router = inject(Router);

  user$ = this.authService.currentUser$;
  
  farms: Farm[] = [];
  loading = true;
  totalParcels = 0;
  totalArea = 0;
  totalUnreadAlerts = 0;
  
  // Chart data
  monthlyChartType: 'line' | 'bar' = 'line';
  private waterUsageChart: Chart | null = null;
  private distributionChart: Chart | null = null;
  private monthlyChart: Chart | null = null;
  
  dashboardStats = {
    totalWaterUsed: 0,
    totalWaterSaved: 0,
    savingsPercentage: 0,
    totalEvents: 0,
    trend: 0,
    weeklyData: [] as number[]
  };

  ngOnInit() {
    this.loadFarms();
  }

  ngAfterViewInit() {
    // Charts will be initialized after farms are loaded
  }

  loadFarms() {
    const user = this.authService.currentUser();
    if (!user) {
      this.loading = false;
      return;
    }

    // Utiliser ownerId par défaut (1) pour le développement
    this.farmService.getFarmsByOwner(1).subscribe({
      next: (farms) => {
        this.farms = farms;
        this.totalParcels = farms.reduce((sum, f) => sum + (f.parcels?.length || 0), 0);
        this.totalArea = farms.reduce((sum, f) => sum + (f.totalArea || 0), 0);
        this.loading = false;
        
        // Load alerts for first farm
        if (farms.length > 0) {
          this.loadAlerts(farms[0].id);
          this.loadChartData(farms[0].id);
        }
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  loadAlerts(farmId: number) {
    this.alertService.getAlertSummary(farmId).subscribe({
      next: (summary) => {
        this.totalUnreadAlerts = summary.unreadCount ?? 0;
      }
    });
  }

  loadChartData(farmId: number) {
    // Load dashboard stats
    this.irrigationService.getDashboardStats(farmId).subscribe({
      next: (stats) => {
        this.dashboardStats = stats;
      }
    });

    // Load weekly data for bar chart
    this.irrigationService.getDailyChartData(farmId, 7).subscribe({
      next: (data) => {
        setTimeout(() => this.createWaterUsageChart(data.labels, data.waterUsed, data.waterSaved), 100);
      }
    });

    // Load parcel distribution
    this.irrigationService.getParcelDistribution(farmId).subscribe({
      next: (parcels) => {
        const labels = parcels.map(p => p.parcelName);
        const values = parcels.map(p => p.totalWaterUsed);
        setTimeout(() => this.createDistributionChart(labels, values), 100);
      }
    });

    // Load monthly data
    this.irrigationService.getMonthlyChartData(farmId, 6).subscribe({
      next: (data) => {
        setTimeout(() => this.createMonthlyChart(data.labels, data.waterUsed, data.waterSaved), 100);
      }
    });
  }

  createWaterUsageChart(labels: string[], waterUsed: number[], waterSaved: number[]) {
    if (!this.waterUsageChartRef) return;
    
    const ctx = this.waterUsageChartRef.nativeElement.getContext('2d');
    if (!ctx) return;

    if (this.waterUsageChart) {
      this.waterUsageChart.destroy();
    }

    this.waterUsageChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            label: 'Eau utilisée (L)',
            data: waterUsed,
            backgroundColor: 'rgba(59, 130, 246, 0.8)',
            borderRadius: 4
          },
          {
            label: 'Eau économisée (L)',
            data: waterSaved,
            backgroundColor: 'rgba(16, 185, 129, 0.8)',
            borderRadius: 4
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            backgroundColor: 'rgba(17, 24, 39, 0.9)',
            padding: 12
          }
        },
        scales: {
          x: { grid: { display: false } },
          y: { 
            beginAtZero: true,
            grid: { color: 'rgba(107, 114, 128, 0.1)' }
          }
        }
      }
    });
  }

  createDistributionChart(labels: string[], values: number[]) {
    if (!this.distributionChartRef) return;
    
    const ctx = this.distributionChartRef.nativeElement.getContext('2d');
    if (!ctx) return;

    if (this.distributionChart) {
      this.distributionChart.destroy();
    }

    this.distributionChart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels,
        datasets: [{
          data: values,
          backgroundColor: [
            'rgba(59, 130, 246, 0.8)',
            'rgba(16, 185, 129, 0.8)',
            'rgba(245, 158, 11, 0.8)',
            'rgba(239, 68, 68, 0.8)',
            'rgba(139, 92, 246, 0.8)'
          ],
          borderWidth: 0
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '65%',
        plugins: {
          legend: {
            position: 'right',
            labels: { 
              padding: 15,
              usePointStyle: true
            }
          },
          tooltip: {
            backgroundColor: 'rgba(17, 24, 39, 0.9)',
            padding: 12,
            callbacks: {
              label: (context) => {
                const total = (context.dataset.data as number[]).reduce((a, b) => a + b, 0);
                const percentage = ((context.raw as number) / total * 100).toFixed(1);
                return `${context.label}: ${(context.raw as number).toLocaleString()} L (${percentage}%)`;
              }
            }
          }
        }
      }
    });
  }

  createMonthlyChart(labels: string[], waterUsed: number[], waterSaved: number[]) {
    if (!this.monthlyChartRef) return;
    
    const ctx = this.monthlyChartRef.nativeElement.getContext('2d');
    if (!ctx) return;

    if (this.monthlyChart) {
      this.monthlyChart.destroy();
    }

    this.monthlyChart = new Chart(ctx, {
      type: this.monthlyChartType,
      data: {
        labels,
        datasets: [
          {
            label: 'Eau utilisée (L)',
            data: waterUsed,
            backgroundColor: this.monthlyChartType === 'line' ? 'rgba(59, 130, 246, 0.1)' : 'rgba(59, 130, 246, 0.8)',
            borderColor: 'rgba(59, 130, 246, 1)',
            borderWidth: 2,
            fill: this.monthlyChartType === 'line',
            tension: 0.4
          },
          {
            label: 'Eau économisée (L)',
            data: waterSaved,
            backgroundColor: this.monthlyChartType === 'line' ? 'rgba(16, 185, 129, 0.1)' : 'rgba(16, 185, 129, 0.8)',
            borderColor: 'rgba(16, 185, 129, 1)',
            borderWidth: 2,
            fill: this.monthlyChartType === 'line',
            tension: 0.4
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'top',
            labels: { usePointStyle: true, padding: 20 }
          },
          tooltip: {
            backgroundColor: 'rgba(17, 24, 39, 0.9)',
            padding: 12
          }
        },
        scales: {
          x: { grid: { display: false } },
          y: { 
            beginAtZero: true,
            grid: { color: 'rgba(107, 114, 128, 0.1)' },
            ticks: {
              callback: (value) => (value as number).toLocaleString() + ' L'
            }
          }
        }
      }
    });
  }

  toggleChartType() {
    this.monthlyChartType = this.monthlyChartType === 'line' ? 'bar' : 'line';
    if (this.farms.length > 0) {
      this.irrigationService.getMonthlyChartData(this.farms[0].id, 6).subscribe({
        next: (data) => {
          this.createMonthlyChart(data.labels, data.waterUsed, data.waterSaved);
        }
      });
    }
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
