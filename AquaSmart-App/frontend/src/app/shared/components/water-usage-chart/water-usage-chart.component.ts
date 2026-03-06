import { Component, Input, OnChanges, SimpleChanges, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Chart, ChartConfiguration, ChartType, registerables } from 'chart.js';

// Register all Chart.js components
Chart.register(...registerables);

export interface ChartDataPoint {
  label: string;
  value: number;
  secondaryValue?: number;
}

@Component({
  selector: 'app-water-usage-chart',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="bg-white rounded-xl shadow-sm p-6">
      <div class="flex justify-between items-center mb-4">
        <h3 class="text-lg font-semibold text-gray-800">{{ title }}</h3>
        <div *ngIf="showPeriodSelector" class="flex gap-2">
          <button 
            *ngFor="let period of periods"
            (click)="onPeriodChange(period)"
            [class.bg-primary]="selectedPeriod === period"
            [class.text-white]="selectedPeriod === period"
            [class.bg-gray-100]="selectedPeriod !== period"
            [class.text-gray-600]="selectedPeriod !== period"
            class="px-3 py-1 rounded-lg text-sm font-medium transition-colors">
            {{ period }}
          </button>
        </div>
      </div>
      
      <div class="relative" [style.height.px]="height">
        <canvas #chartCanvas></canvas>
      </div>
      
      <!-- Legend -->
      <div *ngIf="showLegend" class="flex justify-center gap-6 mt-4">
        <div class="flex items-center gap-2">
          <div class="w-3 h-3 rounded-full bg-blue-500"></div>
          <span class="text-sm text-gray-600">Eau utilisée</span>
        </div>
        <div *ngIf="showSavings" class="flex items-center gap-2">
          <div class="w-3 h-3 rounded-full bg-green-500"></div>
          <span class="text-sm text-gray-600">Eau économisée</span>
        </div>
      </div>
    </div>
  `
})
export class WaterUsageChartComponent implements AfterViewInit, OnChanges {
  @ViewChild('chartCanvas') chartCanvas!: ElementRef<HTMLCanvasElement>;
  
  @Input() title = 'Consommation d\'eau';
  @Input() chartType: 'line' | 'bar' | 'doughnut' = 'bar';
  @Input() labels: string[] = [];
  @Input() data: number[] = [];
  @Input() secondaryData: number[] = [];
  @Input() showSavings = true;
  @Input() showLegend = true;
  @Input() showPeriodSelector = false;
  @Input() height = 300;
  @Input() unit = 'L';

  periods = ['7j', '30j', '90j'];
  selectedPeriod = '7j';
  
  private chart: Chart | null = null;

  ngAfterViewInit() {
    this.createChart();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (this.chart && (changes['data'] || changes['secondaryData'] || changes['labels'])) {
      this.updateChart();
    }
  }

  onPeriodChange(period: string) {
    this.selectedPeriod = period;
    // Émettre l'événement pour que le parent puisse charger les nouvelles données
  }

  private createChart() {
    if (!this.chartCanvas) return;
    
    const ctx = this.chartCanvas.nativeElement.getContext('2d');
    if (!ctx) return;

    const config = this.getChartConfig();
    this.chart = new Chart(ctx, config);
  }

  private updateChart() {
    if (!this.chart) return;
    
    this.chart.data.labels = this.labels;
    this.chart.data.datasets[0].data = this.data;
    if (this.chart.data.datasets[1] && this.secondaryData.length) {
      this.chart.data.datasets[1].data = this.secondaryData;
    }
    this.chart.update();
  }

  private getChartConfig(): ChartConfiguration {
    const baseConfig: ChartConfiguration = {
      type: this.chartType as ChartType,
      data: {
        labels: this.labels,
        datasets: this.getDatasets()
      },
      options: this.getChartOptions()
    };
    
    return baseConfig;
  }

  private getDatasets() {
    if (this.chartType === 'doughnut') {
      return [{
        data: this.data,
        backgroundColor: [
          'rgba(59, 130, 246, 0.8)',
          'rgba(16, 185, 129, 0.8)',
          'rgba(245, 158, 11, 0.8)',
          'rgba(239, 68, 68, 0.8)',
          'rgba(139, 92, 246, 0.8)',
          'rgba(236, 72, 153, 0.8)'
        ],
        borderWidth: 0
      }];
    }

    const datasets: any[] = [{
      label: 'Eau utilisée (' + this.unit + ')',
      data: this.data,
      backgroundColor: this.chartType === 'line' 
        ? 'rgba(59, 130, 246, 0.1)' 
        : 'rgba(59, 130, 246, 0.8)',
      borderColor: 'rgba(59, 130, 246, 1)',
      borderWidth: 2,
      fill: this.chartType === 'line',
      tension: 0.4
    }];

    if (this.showSavings && this.secondaryData.length) {
      datasets.push({
        label: 'Eau économisée (' + this.unit + ')',
        data: this.secondaryData,
        backgroundColor: this.chartType === 'line' 
          ? 'rgba(16, 185, 129, 0.1)' 
          : 'rgba(16, 185, 129, 0.8)',
        borderColor: 'rgba(16, 185, 129, 1)',
        borderWidth: 2,
        fill: this.chartType === 'line',
        tension: 0.4
      });
    }

    return datasets;
  }

  private getChartOptions(): any {
    const baseOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        },
        tooltip: {
          backgroundColor: 'rgba(17, 24, 39, 0.9)',
          padding: 12,
          titleFont: { size: 14 },
          bodyFont: { size: 13 },
          callbacks: {
            label: (context: any) => {
              return `${context.dataset.label}: ${context.raw.toLocaleString()} ${this.unit}`;
            }
          }
        }
      }
    };

    if (this.chartType === 'doughnut') {
      return {
        ...baseOptions,
        cutout: '65%',
        plugins: {
          ...baseOptions.plugins,
          tooltip: {
            ...baseOptions.plugins.tooltip,
            callbacks: {
              label: (context: any) => {
                const total = context.dataset.data.reduce((a: number, b: number) => a + b, 0);
                const percentage = ((context.raw / total) * 100).toFixed(1);
                return `${context.label}: ${context.raw.toLocaleString()} ${this.unit} (${percentage}%)`;
              }
            }
          }
        }
      };
    }

    return {
      ...baseOptions,
      scales: {
        x: {
          grid: { display: false },
          ticks: { color: '#6B7280' }
        },
        y: {
          beginAtZero: true,
          grid: { color: 'rgba(107, 114, 128, 0.1)' },
          ticks: { 
            color: '#6B7280',
            callback: (value: number) => value.toLocaleString()
          }
        }
      }
    };
  }
}
