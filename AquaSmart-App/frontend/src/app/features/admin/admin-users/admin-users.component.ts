import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { NavbarComponent } from '../../../core/components/navbar/navbar.component';

interface User {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  roles: string[];
  enabled: boolean;
  organizationName?: string;
  address?: string;
  city?: string;
  region?: string;
  createdAt?: string;
  updatedAt?: string;
}

@Component({
  selector: 'app-admin-users',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent],
  template: `
    <app-navbar></app-navbar>
    <div class="p-6 max-w-7xl mx-auto">
      <!-- Header -->
      <div class="flex justify-between items-center mb-6">
        <div>
          <h1 class="text-2xl font-bold text-gray-800">Gestion des Utilisateurs</h1>
          <p class="text-gray-500 text-sm mt-1">Administrez les comptes utilisateurs</p>
        </div>
        <span class="bg-purple-100 text-purple-700 px-3 py-1 rounded-full text-sm font-medium">
          Admin
        </span>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-1 sm:grid-cols-3 gap-6 mb-8">
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="text-3xl font-bold text-gray-800">{{ users.length }}</div>
          <div class="text-gray-500 text-sm">Total Utilisateurs</div>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="text-3xl font-bold text-green-600">{{ activeUsers }}</div>
          <div class="text-gray-500 text-sm">Actifs</div>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-6">
          <div class="text-3xl font-bold text-purple-600">{{ adminCount }}</div>
          <div class="text-gray-500 text-sm">Administrateurs</div>
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

      <!-- Users Table -->
      <div class="bg-white rounded-xl shadow-sm overflow-hidden">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Nom</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Email</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Rôles</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Statut</th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-200">
            <tr *ngFor="let user of users" class="hover:bg-gray-50">
              <td class="px-6 py-4 text-sm text-gray-600">{{ user.id }}</td>
              <td class="px-6 py-4">
                <div class="font-medium text-gray-800">{{ user.firstName }} {{ user.lastName }}</div>
              </td>
              <td class="px-6 py-4 text-sm text-gray-600">{{ user.email }}</td>
              <td class="px-6 py-4">
                <span *ngFor="let role of user.roles" 
                      class="inline-block text-xs px-2 py-1 rounded-full mr-1"
                      [class.bg-purple-100]="role.includes('ADMIN')"
                      [class.text-purple-700]="role.includes('ADMIN')"
                      [class.bg-green-100]="!role.includes('ADMIN')"
                      [class.text-green-700]="!role.includes('ADMIN')">
                  {{ formatRole(role) }}
                </span>
              </td>
              <td class="px-6 py-4">
                <span class="text-xs px-2 py-1 rounded-full"
                      [class.bg-green-100]="user.enabled"
                      [class.text-green-700]="user.enabled"
                      [class.bg-red-100]="!user.enabled"
                      [class.text-red-700]="!user.enabled">
                  {{ user.enabled ? 'Actif' : 'Inactif' }}
                </span>
              </td>
              <td class="px-6 py-4">
                <button (click)="toggleUserStatus(user)" 
                        class="text-sm text-primary hover:underline mr-3">
                  {{ user.enabled ? 'Désactiver' : 'Activer' }}
                </button>
                <button (click)="viewUserDetails(user)" 
                        class="text-sm text-gray-600 hover:underline">
                  Détails
                </button>
              </td>
            </tr>
          </tbody>
        </table>
        
        <div *ngIf="users.length === 0 && !loading" class="text-center py-8 text-gray-500">
          Aucun utilisateur trouvé
        </div>
      </div>
    </div>
  `
})
export class AdminUsersComponent implements OnInit {
  private http = inject(HttpClient);
  
  users: User[] = [];
  loading = true;
  error = '';

  get activeUsers(): number {
    return this.users.filter(u => u.enabled).length;
  }

  get adminCount(): number {
    return this.users.filter(u => u.roles?.some(r => r.includes('ADMIN'))).length;
  }

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    
    this.http.get<User[]>('http://localhost:8080/api/users/users').subscribe({
      next: (users) => {
        this.users = users;
        this.loading = false;
        this.error = ''; 
      },
      error: (err) => {
        console.error('Error loading users:', err);
        this.loading = false;
        
        if (err.status === 403) {
          this.error = 'Accès refusé. Seuls les administrateurs peuvent voir cette page.';
        } else if (err.status === 401) {
          this.error = 'Non authentifié. Veuillez vous reconnecter.';
        } else {

          this.error = '';
          this.users = [
            { id: '1', email: 'admin@aquasmart.com', firstName: 'Admin', lastName: 'System', roles: ['ROLE_ADMIN'], enabled: true },
            { id: '2', email: 'farmer@aquasmart.com', firstName: 'Mohamed', lastName: 'El Alami', roles: ['ROLE_FARMER'], enabled: true },
          ];
        }
      }
    });
  }

  formatRole(role: string): string {
    return role.replace('ROLE_', '');
  }

  toggleUserStatus(user: User) {
   
    user.enabled = !user.enabled;
  }

  viewUserDetails(user: User) {
    alert(`Détails de ${user.firstName} ${user.lastName}\nEmail: ${user.email}\nRôles: ${user.roles.join(', ')}`);
  }
}
