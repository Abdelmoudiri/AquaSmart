import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-stats-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="bg-white rounded-xl shadow-sm p-6 hover:shadow-md transition-shadow">
      <div class="flex items-center justify-between">
        <div>
          <p class="text-gray-500 text-sm font-medium">{{ label }}</p>
          <p class="text-2xl font-bold mt-1" [ngClass]="valueClass">
            {{ formattedValue }}
            <span class="text-base font-normal text-gray-500">{{ unit }}</span>
          </p>
          
          <!-- Trend indicator -->
          <div *ngIf="trend !== undefined" class="flex items-center gap-1 mt-2">
            <span [ngClass]="{
              'text-green-500': trend > 0,
              'text-red-500': trend < 0,
              'text-gray-400': trend === 0
            }">
              <svg *ngIf="trend > 0" class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M5.293 9.707a1 1 0 010-1.414l4-4a1 1 0 011.414 0l4 4a1 1 0 01-1.414 1.414L11 7.414V15a1 1 0 11-2 0V7.414L6.707 9.707a1 1 0 01-1.414 0z" clip-rule="evenodd"/>
              </svg>
              <svg *ngIf="trend < 0" class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M14.707 10.293a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 111.414-1.414L9 12.586V5a1 1 0 012 0v7.586l2.293-2.293a1 1 0 011.414 0z" clip-rule="evenodd"/>
              </svg>
              <span *ngIf="trend === 0">—</span>
            </span>
            <span class="text-xs" [ngClass]="{
              'text-green-600': (trendPositiveIsGood && trend > 0) || (!trendPositiveIsGood && trend < 0),
              'text-red-600': (trendPositiveIsGood && trend < 0) || (!trendPositiveIsGood && trend > 0),
              'text-gray-500': trend === 0
            }">
              {{ trend > 0 ? '+' : '' }}{{ trend }}% vs période précédente
            </span>
          </div>
        </div>
        
        <div class="text-4xl">{{ icon }}</div>
      </div>
      
      <!-- Mini sparkline (optional) -->
      <div *ngIf="sparklineData.length > 0" class="mt-4 h-12">
        <svg class="w-full h-full" preserveAspectRatio="none">
          <polyline
            [attr.points]="sparklinePoints"
            fill="none"
            [attr.stroke]="sparklineColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </div>
    </div>
  `
})
export class StatsCardComponent {
  @Input() label = '';
  @Input() value: number = 0;
  @Input() unit = '';
  @Input() icon = '📊';
  @Input() trend?: number;
  @Input() trendPositiveIsGood = true;
  @Input() valueClass = 'text-gray-800';
  @Input() sparklineData: number[] = [];
  @Input() sparklineColor = '#3B82F6';

  get formattedValue(): string {
    if (this.value >= 1000000) {
      return (this.value / 1000000).toFixed(1) + 'M';
    } else if (this.value >= 1000) {
      return (this.value / 1000).toFixed(1) + 'k';
    }
    return this.value.toLocaleString();
  }

  get sparklinePoints(): string {
    if (!this.sparklineData?.length) return '';
    
    const max = Math.max(...this.sparklineData);
    const min = Math.min(...this.sparklineData);
    const range = max - min || 1;
    const width = 100;
    const height = 48;
    const padding = 4;
    
    return this.sparklineData.map((value, index) => {
      const x = (index / (this.sparklineData.length - 1)) * width;
      const y = height - padding - ((value - min) / range) * (height - padding * 2);
      return `${x},${y}`;
    }).join(' ');
  }
}
