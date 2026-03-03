import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { FarmService } from '../../../core/services/farm.service';
import { AuthService } from '../../../core/services/auth.service';
import { FarmRequest } from '../../../core/models/farm.model';
import { NavbarComponent } from '../../../core/components/navbar/navbar.component';

@Component({
  selector: 'app-farm-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, NavbarComponent],
  template: `
    <app-navbar></app-navbar>
    <div class="p-6 max-w-2xl mx-auto">
      <!-- Header -->
      <div class="mb-6">
        <a routerLink="/farms" class="text-primary hover:underline text-sm mb-2 inline-block">
          ← Retour aux fermes
        </a>
        <h1 class="text-2xl font-bold text-gray-800">
          {{ isEdit ? 'Modifier la ferme' : 'Nouvelle ferme' }}
        </h1>
      </div>

      <!-- Error -->
      <div *ngIf="error" class="bg-red-100 text-red-700 p-4 rounded-lg mb-4">
        {{ error }}
      </div>

      <!-- Form -->
      <form (ngSubmit)="onSubmit()" class="bg-white rounded-xl shadow-sm p-6 space-y-6">
        
        <!-- Name -->
        <div>
          <label for="name" class="block text-sm font-medium text-gray-700 mb-2">
            Nom de la ferme *
          </label>
          <input type="text" 
                 id="name" 
                 [(ngModel)]="form.name" 
                 name="name"
                 required
                 class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary focus:border-primary"
                 placeholder="Ma ferme">
        </div>

        <!-- Location -->
        <div>
          <label for="location" class="block text-sm font-medium text-gray-700 mb-2">
            Localisation *
          </label>
          <input type="text" 
                 id="location" 
                 [(ngModel)]="form.location" 
                 name="location"
                 required
                 class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary focus:border-primary"
                 placeholder="Marrakech, Maroc">
        </div>

        <!-- Total Area -->
        <div>
          <label for="totalArea" class="block text-sm font-medium text-gray-700 mb-2">
            Surface totale (hectares) *
          </label>
          <input type="number" 
                 id="totalArea" 
                 [(ngModel)]="form.totalArea" 
                 name="totalArea"
                 required
                 min="0"
                 step="0.1"
                 class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary focus:border-primary"
                 placeholder="10">
        </div>

        <!-- Coordinates -->
        <div class="grid grid-cols-2 gap-4">
          <div>
            <label for="latitude" class="block text-sm font-medium text-gray-700 mb-2">
              Latitude
            </label>
            <input type="number" 
                   id="latitude" 
                   [(ngModel)]="form.latitude" 
                   name="latitude"
                   step="0.0001"
                   class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary focus:border-primary"
                   placeholder="31.6295">
          </div>
          <div>
            <label for="longitude" class="block text-sm font-medium text-gray-700 mb-2">
              Longitude
            </label>
            <input type="number" 
                   id="longitude" 
                   [(ngModel)]="form.longitude" 
                   name="longitude"
                   step="0.0001"
                   class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary focus:border-primary"
                   placeholder="-7.9811">
          </div>
        </div>

        <!-- Description -->
        <div>
          <label for="description" class="block text-sm font-medium text-gray-700 mb-2">
            Description
          </label>
          <textarea id="description" 
                    [(ngModel)]="form.description" 
                    name="description"
                    rows="3"
                    class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary focus:border-primary"
                    placeholder="Description de la ferme..."></textarea>
        </div>

        <!-- Actions -->
        <div class="flex gap-4 pt-4">
          <button type="submit" 
                  [disabled]="submitting"
                  class="flex-1 bg-primary text-white py-2 rounded-lg hover:bg-primary-dark transition-colors disabled:opacity-50">
            {{ submitting ? 'Enregistrement...' : (isEdit ? 'Mettre à jour' : 'Créer') }}
          </button>
          <a routerLink="/farms" 
             class="flex-1 text-center bg-gray-100 text-gray-700 py-2 rounded-lg hover:bg-gray-200 transition-colors">
            Annuler
          </a>
        </div>
      </form>
    </div>
  `
})
export class FarmFormComponent implements OnInit {
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private farmService = inject(FarmService);
  private authService = inject(AuthService);

  isEdit = false;
  farmId: number | null = null;
  submitting = false;
  error = '';

  form: FarmRequest = {
    name: '',
    location: '',
    totalArea: 0,
    latitude: undefined,
    longitude: undefined,
    description: ''
  };

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('farmId') || this.route.snapshot.paramMap.get('id');
    if (id && id !== 'new') {
      this.isEdit = true;
      this.farmId = +id;
      this.loadFarm(this.farmId);
    }
  }

  loadFarm(id: number) {
    this.farmService.getFarmById(id).subscribe({
      next: (farm) => {
        this.form = {
          name: farm.name,
          location: farm.location,
          totalArea: farm.totalArea,
          latitude: farm.latitude,
          longitude: farm.longitude,
          description: farm.description || ''
        };
      },
      error: () => {
        this.error = 'Ferme non trouvée';
      }
    });
  }

  onSubmit() {
    const user = this.authService.currentUser();
    if (!user) {
      this.error = 'Utilisateur non connecté';
      return;
    }

    if (!this.form.name || !this.form.location || !this.form.totalArea) {
      this.error = 'Veuillez remplir tous les champs obligatoires';
      return;
    }

    this.submitting = true;
    this.error = '';

    // Note: ownerId sera géré par le backend via header ou défaut
    const request = { ...this.form };

    const operation = this.isEdit && this.farmId
      ? this.farmService.updateFarm(this.farmId, request)
      : this.farmService.createFarm(request);

    operation.subscribe({
      next: (farm) => {
        this.router.navigate(['/farms', farm.id]);
      },
      error: (err) => {
        console.error('Farm creation error:', err);
        if (err.status === 0) {
          this.error = 'Impossible de joindre le serveur. Vérifiez que le backend est démarré.';
        } else if (err.error?.message) {
          this.error = err.error.message;
        } else if (err.message) {
          this.error = err.message;
        } else {
          this.error = `Erreur ${err.status || 'inconnue'} lors de l'enregistrement`;
        }
        this.submitting = false;
      }
    });
  }
}
