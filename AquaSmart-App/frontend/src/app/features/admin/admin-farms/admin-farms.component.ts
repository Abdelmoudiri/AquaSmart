import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { NavbarComponent } from '../../../core/components/navbar/navbar.component';

interface Farm {
  id: number;
  name: string;
  location: string;
  ownerId: number;
  ownerName?: string;
  totalArea: number;
  parcelCount?: number;
  status?: string;
  createdAt?: string;
}

@Component({
  selector: 'app-admin-farms',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent],
  template: `
    <app-navbar></app-navbar>
    <div class="p-6 max-w-7xl mx-auto">
      <!-- Header -->
      <div class="flex justify-between items-center mb-6">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Toutes les Fermes</h1>
          <p class="text-gray-500 text-sm mt-1">Vue administrateur de toutes les fermes du système</p>
        </div>
        <span class="bg-purple-100 text-purple-700 px-3 py-1 rounded-full text-sm font-medium">
          Admin
        </span>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-1 sm:grid-cols-4 gap-6 mb-8">
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="text-3xl font-bold text-gray-800">{{ farms.length }}</div>
          <div class="text-gray-500 text-sm">Total Fermes</div>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="text-3xl font-bold text-green-600">{{ totalArea | number:'1.0-1' }}</div>
          <div class="text-gray-500 text-sm">Hectares Total</div>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="text-3xl font-bold text-blue-600">{{ totalParcels }}</div>
          <div class="text-gray-500 text-sm">Total Parcelles</div>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="text-3xl font-bold text-purple-600">{{ uniqueOwners }}</div>
          <div class="text-gray-500 text-sm">Propriétaires</div>
        </div>
      </div>

      <!-- Loading -->
      <div *ngIf="loading" class="text-center py-8">
        <p class="text-gray-500">Chargement...</p>
      </div>

      <!-- Error -->
      <div *ngIf="error" class="bg-red-100 text-red-700 p-4 rounded-lg mb-4">
        {{ error }}
      </div>

      <!-- Farms Table -->
      <div class="bg-white rounded-xl shadow-sm overflow-hidden">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Nom</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Localisation</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Propriétaire</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Surface</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Parcelles</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            <tr *ngFor="let farm of farms" class="hover:bg-gray-50">
              <td class="px-6 py-4 text-sm text-gray-600">{{ farm.id }}</td>
              <td class="px-6 py-4">
                <div class="font-medium text-gray-800">{{ farm.name }}</div>
              </td>
              <td class="px-6 py-4 text-sm text-gray-600">{{ farm.location }}</td>
              <td class="px-6 py-4 text-sm text-gray-600">
                <span class="bg-gray-100 px-2 py-1 rounded text-xs">
                  User #{{ farm.ownerId }}
                </span>
              </td>
              <td class="px-6 py-4 text-sm text-gray-600">{{ farm.totalArea }} ha</td>
              <td class="px-6 py-4 text-sm text-gray-600">{{ farm.parcelCount || 0 }}</td>
              <td class="px-6 py-4">
                <span class="text-xs px-2 py-1 rounded-full"
                      [class.bg-green-100]="farm.status === 'ACTIVE'"
                      [class.text-green-700]="farm.status === 'ACTIVE'"
                      [class.bg-yellow-100]="farm.status === 'UNDER_CONSTRUCTION'"
                      [class.text-yellow-700]="farm.status === 'UNDER_CONSTRUCTION'"
                      [class.bg-gray-100]="!farm.status || farm.status === 'INACTIVE'"
                      [class.text-gray-600]="!farm.status || farm.status === 'INACTIVE'">
                  {{ farm.status || 'N/A' }}
                </span>
              </td>
              <td class="px-6 py-4">
                <a [routerLink]="['/farms', farm.id]" 
                   class="text-sm text-primary hover:underline mr-3">
                  Voir
                </a>
                <button (click)="deleteFarm(farm)" 
                        class="text-sm text-red-500 hover:underline">
                  Supprimer
                </button>
              </td>
            </tr>
          </tbody>
        </table>
        
        <div *ngIf="farms.length === 0 && !loading" class="text-center py-8 text-gray-500">
          Aucune ferme trouvée
        </div>
      </div>
    </div>
  `
})
export class AdminFarmsComponent implements OnInit {
  private http = inject(HttpClient);
  
  farms: Farm[] = [];
  loading = true;
  error = '';

  get totalArea(): number {
    return this.farms.reduce((sum, f) => sum + (f.totalArea || 0), 0);
  }

  get totalParcels(): number {
    return this.farms.reduce((sum, f) => sum + (f.parcelCount || 0), 0);
  }

  get uniqueOwners(): number {
    return new Set(this.farms.map(f => f.ownerId)).size;
  }

  ngOnInit() {
    this.loadAllFarms();
  }

  loadAllFarms() {
    // Admin endpoint to get all farms
    this.http.get<Farm[]>('http://localhost:8080/api/farms').subscribe({
      next: (farms) => {
        this.farms = farms;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading farms:', err);
        this.error = 'Erreur lors du chargement des fermes';
        this.loading = false;
      }
    });
  }

  deleteFarm(farm: Farm) {
    if (confirm(`Êtes-vous sûr de vouloir supprimer la ferme "${farm.name}" ?`)) {
      this.http.delete(`http://localhost:8080/api/farms/${farm.id}`).subscribe({
        next: () => {
          this.farms = this.farms.filter(f => f.id !== farm.id);
        },
        error: (err) => {
          alert('Erreur lors de la suppression');
          console.error(err);
        }
      });
    }
  }
}
