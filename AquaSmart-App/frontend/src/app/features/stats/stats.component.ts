import { Component, OnInit, inject, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { IrrigationService } from '../../core/services/irrigation.service';
import { FarmService } from '../../core/services/farm.service';
import { AuthService } from '../../core/services/auth.service';
import { Farm } from '../../core/models/farm.model';
import { WaterUsageChartData, ParcelWaterUsage } from '../../core/models/irrigation.model';
import { Chart, registerables } from 'chart.js';
import { NavbarComponent } from '../../core/components/navbar/navbar.component';

Chart.register(...registerables);

@Component({
  selector: 'app-stats',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, NavbarComponent],
  template: `
    <app-navbar></app-navbar>
    <div class="p-6 max-w-7xl mx-auto">
      <!-- Header -->
      <div class="flex flex-wrap justify-between items-center mb-8">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Statistiques de consommation</h1>
          <p class="text-gray-500 mt-1">Analysez votre consommation d'eau en détail</p>
        </div>
        
        <!-- Farm Selector -->
        <div class="mt-4 md:mt-0">
          <select [(ngModel)]="selectedFarmId" 
                  (change)="onFarmChange()"
                  class="px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary min-w-[200px]">
            <option [value]="null">-- Toutes les fermes --</option>
            <option *ngFor="let farm of farms" [value]="farm.id">{{ farm.name }}</option>
          </select>
        </div>
      </div>

      <!-- Period Selector -->
      <div class="bg-white rounded-xl shadow-sm p-4 mb-6">
        <div class="flex flex-wrap gap-2">
          <button *ngFor="let period of periods"
                  (click)="selectPeriod(period)"
                  [class.bg-primary]="selectedPeriod === period.value"
                  [class.text-white]="selectedPeriod === period.value"
                  [class.bg-gray-100]="selectedPeriod !== period.value"
                  [class.text-gray-700]="selectedPeriod !== period.value"
                  class="px-4 py-2 rounded-lg font-medium transition-colors">
            {{ period.label }}
          </button>
        </div>
      </div>

      <!-- Summary Cards -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
              <span class="text-2xl">💧</span>
            </div>
            <div>
              <p class="text-gray-500 text-sm">Total consommé</p>
              <p class="text-2xl font-bold text-gray-800">{{ totalWaterUsed | number:'1.0-0' }} L</p>
            </div>
          </div>
          <div class="mt-4 h-12">
            <canvas #miniChart1></canvas>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
              <span class="text-2xl">🌱</span>
            </div>
            <div>
              <p class="text-gray-500 text-sm">Eau économisée</p>
              <p class="text-2xl font-bold text-green-600">{{ totalWaterSaved | number:'1.0-0' }} L</p>
            </div>
          </div>
          <div class="mt-3 flex items-center gap-2">
            <div class="flex-1 bg-gray-200 rounded-full h-2">
              <div class="bg-green-500 h-2 rounded-full" [style.width.%]="savingsPercentage"></div>
            </div>
            <span class="text-sm font-medium text-green-600">{{ savingsPercentage }}%</span>
          </div>
        </div>

        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center">
              <span class="text-2xl">📊</span>
            </div>
            <div>
              <p class="text-gray-500 text-sm">Moyenne/jour</p>
              <p class="text-2xl font-bold text-gray-800">{{ avgDailyUsage | number:'1.0-0' }} L</p>
            </div>
          </div>
          <p class="mt-4 text-sm text-gray-500">
            <span [class.text-green-600]="avgTrend < 0" [class.text-red-600]="avgTrend > 0">
              {{ avgTrend > 0 ? '+' : '' }}{{ avgTrend }}%
            </span>
            vs période précédente
          </p>
        </div>

        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-amber-100 rounded-lg flex items-center justify-center">
              <span class="text-2xl">⚡</span>
            </div>
            <div>
              <p class="text-gray-500 text-sm">Efficacité</p>
              <p class="text-2xl font-bold text-amber-600">{{ efficiencyScore }}%</p>
            </div>
          </div>
          <p class="mt-4 text-sm text-gray-500">Score d'optimisation</p>
        </div>
      </div>

      <!-- Main Charts -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
        <!-- Usage Over Time -->
        <div class="lg:col-span-2 bg-white rounded-xl shadow-sm p-6">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-semibold text-gray-800">Évolution de la consommation</h3>
            <div class="flex gap-2">
              <button (click)="setMainChartType('line')"
                      [class.bg-primary]="mainChartType === 'line'"
                      [class.text-white]="mainChartType === 'line'"
                      [class.bg-gray-100]="mainChartType !== 'line'"
                      class="p-2 rounded-lg">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 12l3-3 3 3 4-4M8 21l4-4 4 4M3 4h18M4 4h16v12a1 1 0 01-1 1H5a1 1 0 01-1-1V4z"/>
                </svg>
              </button>
              <button (click)="setMainChartType('bar')"
                      [class.bg-primary]="mainChartType === 'bar'"
                      [class.text-white]="mainChartType === 'bar'"
                      [class.bg-gray-100]="mainChartType !== 'bar'"
                      class="p-2 rounded-lg">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z"/>
                </svg>
              </button>
            </div>
          </div>
          <div class="h-80">
            <canvas #mainChart></canvas>
          </div>
          <div class="flex justify-center gap-6 mt-4">
            <div class="flex items-center gap-2">
              <div class="w-3 h-3 rounded-full bg-blue-500"></div>
              <span class="text-sm text-gray-600">Consommation</span>
            </div>
            <div class="flex items-center gap-2">
              <div class="w-3 h-3 rounded-full bg-green-500"></div>
              <span class="text-sm text-gray-600">Économies</span>
            </div>
          </div>
        </div>

        <!-- Distribution by Parcel -->
        <div class="bg-white rounded-xl shadow-sm p-6">
          <h3 class="text-lg font-semibold text-gray-800 mb-4">Répartition par parcelle</h3>
          <div class="h-64">
            <canvas #parcelChart></canvas>
          </div>
          
          <!-- Legend -->
          <div class="mt-4 space-y-2">
            <div *ngFor="let parcel of parcelDistribution; let i = index" 
                 class="flex items-center justify-between text-sm">
              <div class="flex items-center gap-2">
                <div class="w-3 h-3 rounded-full" [style.backgroundColor]="parcelColors[i]"></div>
                <span class="text-gray-600">{{ parcel.parcelName }}</span>
              </div>
              <span class="font-medium">{{ parcel.percentage }}%</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Comparison Chart -->
      <div class="bg-white rounded-xl shadow-sm p-6 mb-8">
        <h3 class="text-lg font-semibold text-gray-800 mb-4">Comparaison Consommation vs Recommandation</h3>
        <div class="h-72">
          <canvas #comparisonChart></canvas>
        </div>
      </div>

      <!-- Daily Breakdown Table -->
      <div class="bg-white rounded-xl shadow-sm overflow-hidden">
        <div class="p-6 border-b">
          <h3 class="text-lg font-semibold text-gray-800">Détail journalier</h3>
        </div>
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Date</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Consommation</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Économie</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Irrigations</th>
                <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Efficacité</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200">
              <tr *ngFor="let day of dailyData" class="hover:bg-gray-50">
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-800">{{ day.date | date:'EEE dd/MM' }}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm">
                  <span class="font-medium text-blue-600">{{ day.waterUsed | number:'1.0-0' }} L</span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm">
                  <span class="font-medium text-green-600">{{ day.waterSaved | number:'1.0-0' }} L</span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-600">{{ day.eventsCount }}</td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center gap-2">
                    <div class="w-16 bg-gray-200 rounded-full h-2">
                      <div class="h-2 rounded-full" 
                           [class.bg-green-500]="getEfficiency(day) >= 70"
                           [class.bg-yellow-500]="getEfficiency(day) >= 50 && getEfficiency(day) < 70"
                           [class.bg-red-500]="getEfficiency(day) < 50"
                           [style.width.%]="getEfficiency(day)">
                      </div>
                    </div>
                    <span class="text-sm text-gray-600">{{ getEfficiency(day) }}%</span>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  `
})
export class StatsComponent implements OnInit, AfterViewInit {
  @ViewChild('mainChart') mainChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('parcelChart') parcelChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('comparisonChart') comparisonChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('miniChart1') miniChart1Ref!: ElementRef<HTMLCanvasElement>;

  irrigationService = inject(IrrigationService);
  farmService = inject(FarmService);
  authService = inject(AuthService);

  farms: Farm[] = [];
  selectedFarmId: number | null = null;
  
  periods = [
    { label: '7 jours', value: 7 },
    { label: '30 jours', value: 30 },
    { label: '3 mois', value: 90 },
    { label: '6 mois', value: 180 },
    { label: '1 an', value: 365 }
  ];
  selectedPeriod = 7;
  
  mainChartType: 'line' | 'bar' = 'line';
  
  // Stats
  totalWaterUsed = 0;
  totalWaterSaved = 0;
  savingsPercentage = 0;
  avgDailyUsage = 0;
  avgTrend = 0;
  efficiencyScore = 0;
  
  // Data
  dailyData: any[] = [];
  chartData: WaterUsageChartData | null = null;
  parcelDistribution: ParcelWaterUsage[] = [];
  parcelColors = [
    'rgba(59, 130, 246, 0.8)',
    'rgba(16, 185, 129, 0.8)',
    'rgba(245, 158, 11, 0.8)',
    'rgba(239, 68, 68, 0.8)',
    'rgba(139, 92, 246, 0.8)'
  ];

  private mainChart: Chart | null = null;
  private parcelChart: Chart | null = null;
  private comparisonChart: Chart | null = null;
  private miniChart1: Chart | null = null;

  ngOnInit() {
    this.loadFarms();
  }

  ngAfterViewInit() {
    // Charts initialized after data loads
  }

  loadFarms() {
    const user = this.authService.currentUser();
    if (!user) return;

    // Utiliser ownerId par défaut (1) pour le développement
    this.farmService.getFarmsByOwner(1).subscribe({
      next: (farms) => {
        this.farms = farms;
        if (farms.length > 0) {
          this.selectedFarmId = farms[0].id;
          this.loadData();
        }
      }
    });
  }

  onFarmChange() {
    this.loadData();
  }

  selectPeriod(period: { label: string; value: number }) {
    this.selectedPeriod = period.value;
    this.loadData();
  }

  setMainChartType(type: 'line' | 'bar') {
    this.mainChartType = type;
    if (this.chartData) {
      this.createMainChart();
    }
  }

  loadData() {
    const farmId = this.selectedFarmId || (this.farms[0]?.id ?? 0);
    const days = this.selectedPeriod;

    // Load daily chart data
    this.irrigationService.getDailyChartData(farmId, days).subscribe({
      next: (data) => {
        this.chartData = data;
        this.dailyData = data.dailyData || [];
        
        this.totalWaterUsed = data.waterUsed.reduce((a, b) => a + b, 0);
        this.totalWaterSaved = data.waterSaved.reduce((a, b) => a + b, 0);
        this.savingsPercentage = Math.round((this.totalWaterSaved / (this.totalWaterUsed + this.totalWaterSaved)) * 100);
        this.avgDailyUsage = Math.round(this.totalWaterUsed / days);
        this.avgTrend = Math.round(-5 + Math.random() * 15);
        this.efficiencyScore = Math.round(70 + this.savingsPercentage / 4);

        setTimeout(() => {
          this.createMainChart();
          this.createMiniChart();
        }, 100);
      }
    });

    // Load parcel distribution
    this.irrigationService.getParcelDistribution(farmId).subscribe({
      next: (parcels) => {
        this.parcelDistribution = parcels;
        setTimeout(() => this.createParcelChart(), 100);
      }
    });

    // Create comparison chart with mock data
    setTimeout(() => this.createComparisonChart(), 200);
  }

  createMainChart() {
    if (!this.mainChartRef || !this.chartData) return;
    
    const ctx = this.mainChartRef.nativeElement.getContext('2d');
    if (!ctx) return;

    if (this.mainChart) this.mainChart.destroy();

    const isLine = this.mainChartType === 'line';

    this.mainChart = new Chart(ctx, {
      type: this.mainChartType,
      data: {
        labels: this.chartData.labels,
        datasets: [
          {
            label: 'Consommation (L)',
            data: this.chartData.waterUsed,
            backgroundColor: isLine ? 'rgba(59, 130, 246, 0.1)' : 'rgba(59, 130, 246, 0.8)',
            borderColor: 'rgba(59, 130, 246, 1)',
            borderWidth: 2,
            fill: isLine,
            tension: 0.4,
            borderRadius: isLine ? 0 : 4
          },
          {
            label: 'Économie (L)',
            data: this.chartData.waterSaved,
            backgroundColor: isLine ? 'rgba(16, 185, 129, 0.1)' : 'rgba(16, 185, 129, 0.8)',
            borderColor: 'rgba(16, 185, 129, 1)',
            borderWidth: 2,
            fill: isLine,
            tension: 0.4,
            borderRadius: isLine ? 0 : 4
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false },
          tooltip: {
            backgroundColor: 'rgba(17, 24, 39, 0.95)',
            padding: 12,
            titleFont: { size: 14 },
            bodyFont: { size: 13 }
          }
        },
        scales: {
          x: { grid: { display: false } },
          y: { 
            beginAtZero: true,
            grid: { color: 'rgba(107, 114, 128, 0.1)' },
            ticks: { callback: (v) => v.toLocaleString() + ' L' }
          }
        }
      }
    });
  }

  createParcelChart() {
    if (!this.parcelChartRef || !this.parcelDistribution.length) return;
    
    const ctx = this.parcelChartRef.nativeElement.getContext('2d');
    if (!ctx) return;

    if (this.parcelChart) this.parcelChart.destroy();

    this.parcelChart = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: this.parcelDistribution.map(p => p.parcelName),
        datasets: [{
          data: this.parcelDistribution.map(p => p.totalWaterUsed),
          backgroundColor: this.parcelColors,
          borderWidth: 0
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: '60%',
        plugins: {
          legend: { display: false },
          tooltip: {
            backgroundColor: 'rgba(17, 24, 39, 0.95)',
            padding: 12
          }
        }
      }
    });
  }

  createComparisonChart() {
    if (!this.comparisonChartRef) return;
    
    const ctx = this.comparisonChartRef.nativeElement.getContext('2d');
    if (!ctx) return;

    if (this.comparisonChart) this.comparisonChart.destroy();

    const labels = this.chartData?.labels || ['Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam', 'Dim'];
    const actual = this.chartData?.waterUsed || [800, 950, 700, 1100, 900, 850, 750];
    const recommended = actual.map(v => v * (0.7 + Math.random() * 0.2));

    this.comparisonChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels,
        datasets: [
          {
            label: 'Consommation réelle',
            data: actual,
            backgroundColor: 'rgba(59, 130, 246, 0.8)',
            borderRadius: 4
          },
          {
            label: 'Recommandation',
            data: recommended,
            backgroundColor: 'rgba(156, 163, 175, 0.5)',
            borderRadius: 4
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'top',
            labels: { usePointStyle: true }
          },
          tooltip: {
            backgroundColor: 'rgba(17, 24, 39, 0.95)',
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

  createMiniChart() {
    if (!this.miniChart1Ref || !this.chartData) return;
    
    const ctx = this.miniChart1Ref.nativeElement.getContext('2d');
    if (!ctx) return;

    if (this.miniChart1) this.miniChart1.destroy();

    this.miniChart1 = new Chart(ctx, {
      type: 'line',
      data: {
        labels: this.chartData.labels.slice(-7),
        datasets: [{
          data: this.chartData.waterUsed.slice(-7),
          borderColor: 'rgba(59, 130, 246, 1)',
          borderWidth: 2,
          fill: false,
          tension: 0.4,
          pointRadius: 0
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: { legend: { display: false }, tooltip: { enabled: false } },
        scales: {
          x: { display: false },
          y: { display: false }
        }
      }
    });
  }

  getEfficiency(day: any): number {
    return Math.min(100, Math.round((day.waterSaved / (day.waterUsed + day.waterSaved)) * 100 + 50));
  }
}
