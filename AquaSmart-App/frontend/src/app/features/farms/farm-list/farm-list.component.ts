import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FarmService } from '../../../core/services/farm.service';
import { AuthService } from '../../../core/services/auth.service';
import { Farm } from '../../../core/models/farm.model';
import { NavbarComponent } from '../../../core/components/navbar/navbar.component';

@Component({
  selector: 'app-farm-list',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent],
  template: `
    <app-navbar></app-navbar>
    <div class="p-6">
      <!-- Header -->
      <div class="flex justify-between items-center mb-6">
        <h1 class="text-2xl font-bold text-gray-800">Mes Fermes</h1>
        <a routerLink="/farms/new" 
           class="bg-primary text-white px-4 py-2 rounded-lg hover:bg-primary-dark transition-colors">
          + Nouvelle Ferme
        </a>
      </div>

      <!-- Loading -->
      <div *ngIf="loading" class="text-center py-8">
        <p class="text-gray-500">Chargement...</p>
      </div>

      <!-- Error -->
      <div *ngIf="error" class="bg-red-100 text-red-700 p-4 rounded-lg mb-4">
        {{ error }}
      </div>

      <!-- Empty State -->
      <div *ngIf="!loading && farms.length === 0" class="text-center py-12 bg-white rounded-xl shadow-sm">
        <p class="text-gray-500 mb-4">Vous n'avez pas encore de ferme.</p>
        <a routerLink="/farms/new" class="text-primary hover:underline">
          Créer votre première ferme
        </a>
      </div>

      <!-- Farm Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div *ngFor="let farm of farms" 
             class="bg-white rounded-xl shadow-sm p-6 hover:shadow-md transition-shadow">
          
          <!-- Farm Header -->
          <div class="flex justify-between items-start mb-4">
            <div>
              <h2 class="text-xl font-semibold text-gray-800">{{ farm.name }}</h2>
              <p class="text-gray-500 text-sm">{{ farm.location }}</p>
            </div>
            <span class="text-sm px-2 py-1 rounded-full"
                  [class.bg-green-100]="farm.active"
                  [class.text-green-700]="farm.active"
                  [class.bg-gray-100]="!farm.active"
                  [class.text-gray-600]="!farm.active">
              {{ farm.active ? 'Active' : 'Inactive' }}
            </span>
          </div>

          <!-- Farm Info -->
          <div class="space-y-2 text-sm text-gray-600 mb-4">
            <p><strong>Surface:</strong> {{ farm.totalArea }} ha</p>
            <p><strong>Parcelles:</strong> {{ farm.parcels?.length || 0 }}</p>
            <p *ngIf="farm.latitude && farm.longitude">
              <strong>GPS:</strong> {{ farm.latitude | number:'1.4-4' }}, {{ farm.longitude | number:'1.4-4' }}
            </p>
          </div>

          <!-- Actions -->
          <div class="flex gap-2 pt-4 border-t">
            <a [routerLink]="['/farms', farm.id]" 
               class="flex-1 text-center bg-primary/10 text-primary px-3 py-2 rounded-lg hover:bg-primary/20 transition-colors">
              Voir
            </a>
            <a [routerLink]="['/farms', farm.id, 'edit']" 
               class="flex-1 text-center bg-gray-100 text-gray-700 px-3 py-2 rounded-lg hover:bg-gray-200 transition-colors">
              Modifier
            </a>
          </div>
        </div>
      </div>
    </div>
  `
})
export class FarmListComponent implements OnInit {
  private farmService = inject(FarmService);
  private authService = inject(AuthService);

  farms: Farm[] = [];
  loading = true;
  error = '';

  ngOnInit() {
    this.loadFarms();
  }

  loadFarms() {
    const user = this.authService.currentUser();
    if (!user) {
      this.error = 'Utilisateur non connecté';
      this.loading = false;
      return;
    }

    // Utiliser ownerId par défaut (1) pour le développement
    this.farmService.getFarmsByOwner(1).subscribe({
      next: (farms) => {
        this.farms = farms;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des fermes';
        this.loading = false;
        console.error(err);
      }
    });
  }
}
