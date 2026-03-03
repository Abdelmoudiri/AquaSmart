import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { FarmService } from '../../../core/services/farm.service';
import { ParcelRequest } from '../../../core/models/farm.model';
import { NavbarComponent } from '../../../core/components/navbar/navbar.component';

@Component({
  selector: 'app-parcel-form',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, NavbarComponent],
  template: `
    <app-navbar></app-navbar>
    <div class="p-6 max-w-2xl mx-auto">
      <!-- Header -->
      <div class="mb-6">
        <a [routerLink]="['/farms', farmId]" class="text-primary hover:underline text-sm mb-2 inline-block">
          ← Retour à la ferme
        </a>
        <h1 class="text-2xl font-bold text-gray-800">
          {{ isEdit ? 'Modifier la parcelle' : 'Nouvelle parcelle' }}
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
            Nom de la parcelle *
          </label>
          <input type="text" 
                 id="name" 
                 [(ngModel)]="form.name" 
                 name="name"
                 required
                 class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary focus:border-primary"
                 placeholder="Parcelle Nord">
        </div>

        <!-- Area -->
        <div>
          <label for="area" class="block text-sm font-medium text-gray-700 mb-2">
            Surface (hectares) *
          </label>
          <input type="number" 
                 id="area" 
                 [(ngModel)]="form.area" 
                 name="area"
                 required
                 min="0"
                 step="0.1"
                 class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary focus:border-primary"
                 placeholder="2.5">
        </div>

        <!-- Soil Type -->
        <div>
          <label for="soilType" class="block text-sm font-medium text-gray-700 mb-2">
            Type de sol *
          </label>
          <select id="soilType" 
                  [(ngModel)]="form.soilType" 
                  name="soilType"
                  required
                  class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary focus:border-primary">
            <option value="">Sélectionner un type</option>
            <option value="CLAY">Argileux</option>
            <option value="SANDY">Sableux</option>
            <option value="LOAMY">Limoneux</option>
            <option value="SILTY">Limoneux fin</option>
            <option value="CHALKY">Calcaire</option>
            <option value="PEATY">Tourbeux</option>
            <option value="MIXED">Mixte</option>
          </select>
        </div>

        <!-- Irrigation Type -->
        <div>
          <label for="irrigationType" class="block text-sm font-medium text-gray-700 mb-2">
            Type d'irrigation *
          </label>
          <select id="irrigationType" 
                  [(ngModel)]="form.irrigationType" 
                  name="irrigationType"
                  required
                  class="w-full px-4 py-2 border rounded-lg focus:ring-2 focus:ring-primary focus:border-primary">
            <option value="">Sélectionner un type</option>
            <option value="DRIP">Goutte à goutte</option>
            <option value="SPRINKLER">Aspersion</option>
            <option value="SURFACE">Gravitaire</option>
            <option value="SUBSURFACE">Sous-surface</option>
            <option value="PIVOT">Pivot central</option>
            <option value="MANUAL">Manuel</option>
          </select>
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
                    placeholder="Description de la parcelle..."></textarea>
        </div>

        <!-- Actions -->
        <div class="flex gap-4 pt-4">
          <button type="submit" 
                  [disabled]="submitting"
                  class="flex-1 bg-primary text-white py-2 rounded-lg hover:bg-primary-dark transition-colors disabled:opacity-50">
            {{ submitting ? 'Enregistrement...' : (isEdit ? 'Mettre à jour' : 'Créer') }}
          </button>
          <a [routerLink]="['/farms', farmId]" 
             class="flex-1 text-center bg-gray-100 text-gray-700 py-2 rounded-lg hover:bg-gray-200 transition-colors">
            Annuler
          </a>
        </div>
      </form>
    </div>
  `
})
export class ParcelFormComponent implements OnInit {
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private farmService = inject(FarmService);

  isEdit = false;
  farmId: number = 0;
  parcelId: number | null = null;
  submitting = false;
  error = '';

  form: ParcelRequest = {
    farmId: 0,
    name: '',
    area: 0,
    soilType: '',
    irrigationType: '',
    description: '',
    latitude: undefined,
    longitude: undefined
  };

  ngOnInit() {
    // Get farm ID from route
    const farmIdParam = this.route.snapshot.paramMap.get('farmId');
    if (farmIdParam) {
      this.farmId = +farmIdParam;
      this.form.farmId = this.farmId;
    }

    // Check if editing
    const parcelIdParam = this.route.snapshot.paramMap.get('parcelId');
    if (parcelIdParam && parcelIdParam !== 'new') {
      this.isEdit = true;
      this.parcelId = +parcelIdParam;
      this.loadParcel(this.parcelId);
    }
  }

  loadParcel(id: number) {
    this.farmService.getParcelById(id, this.farmId).subscribe({
      next: (parcel) => {
        this.form = {
          farmId: parcel.farmId,
          name: parcel.name,
          area: parcel.area,
          soilType: parcel.soilType,
          irrigationType: parcel.irrigationType,
          description: parcel.description || '',
          latitude: parcel.latitude,
          longitude: parcel.longitude
        };
      },
      error: () => {
        this.error = 'Parcelle non trouvée';
      }
    });
  }

  onSubmit() {
    if (!this.form.name || !this.form.area || !this.form.soilType || !this.form.irrigationType) {
      this.error = 'Veuillez remplir tous les champs obligatoires';
      return;
    }

    this.submitting = true;
    this.error = '';

    const operation = this.isEdit && this.parcelId
      ? this.farmService.updateParcel(this.parcelId, this.form)
      : this.farmService.createParcel(this.form);

    operation.subscribe({
      next: () => {
        this.router.navigate(['/farms', this.farmId]);
      },
      error: (err) => {
        console.error('Parcel error:', err);
        if (err.status === 0) {
          this.error = 'Impossible de joindre le serveur. Vérifiez que le backend est démarré.';
        } else if (err.error?.message) {
          this.error = err.error.message;
        } else {
          this.error = `Erreur ${err.status || 'inconnue'} lors de l'enregistrement`;
        }
        this.submitting = false;
      }
    });
  }
}
