import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="bg-white shadow-sm sticky top-0 z-50">
      <div class="max-w-7xl mx-auto px-4">
        <div class="flex justify-between items-center h-16">
          <!-- Logo -->
          <a routerLink="/dashboard" class="flex items-center gap-2 text-2xl font-bold text-primary-dark hover:opacity-80 transition-opacity">
            <svg class="w-8 h-8 text-primary" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.94-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z"/>
            </svg>
            <span>AquaSmart</span>
          </a>
          
          <!-- Menu -->
          <div class="hidden md:flex items-center space-x-6">
            <a routerLink="/dashboard" routerLinkActive="text-primary" [routerLinkActiveOptions]="{exact: true}"
               class="text-gray-700 hover:text-primary font-medium transition-colors">
              Dashboard
            </a>
            
            <!-- Admin Only Links -->
            <ng-container *ngIf="isAdmin">
              <a routerLink="/admin/users" routerLinkActive="text-primary"
                 class="text-gray-700 hover:text-primary font-medium transition-colors">
                Utilisateurs
              </a>
              <a routerLink="/admin/farms" routerLinkActive="text-primary"
                 class="text-gray-700 hover:text-primary font-medium transition-colors">
                Toutes les Fermes
              </a>
            </ng-container>
            
            <!-- Farmer Links -->
            <a *ngIf="!isAdmin" routerLink="/farms" routerLinkActive="text-primary"
               class="text-gray-700 hover:text-primary font-medium transition-colors">
              Mes Fermes
            </a>
            <a routerLink="/irrigation" routerLinkActive="text-primary"
               class="text-gray-700 hover:text-primary font-medium transition-colors">
              Irrigation
            </a>
            <a routerLink="/stats" routerLinkActive="text-primary"
               class="text-gray-700 hover:text-primary font-medium transition-colors">
              Stats
            </a>
            <a routerLink="/weather" routerLinkActive="text-primary"
               class="text-gray-700 hover:text-primary font-medium transition-colors">
              Météo
            </a>
            <a routerLink="/alerts" routerLinkActive="text-primary"
               class="text-gray-700 hover:text-primary font-medium transition-colors">
              Alertes
            </a>
          </div>
          
          <!-- Mobile menu button -->
          <button (click)="mobileMenuOpen = !mobileMenuOpen" 
                  class="md:hidden p-2 rounded-lg text-gray-600 hover:bg-gray-100">
            <svg *ngIf="!mobileMenuOpen" class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16M4 18h16"/>
            </svg>
            <svg *ngIf="mobileMenuOpen" class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
            </svg>
          </button>
          
          <!-- User -->
          <div class="hidden md:flex items-center gap-4">
            <div *ngIf="user$ | async as user" class="flex items-center gap-2">
              <span class="text-gray-600 text-sm">{{ user.firstName }}</span>
              <span class="text-xs px-2 py-1 rounded-full"
                    [class.bg-purple-100]="isAdmin"
                    [class.text-purple-700]="isAdmin"
                    [class.bg-green-100]="!isAdmin"
                    [class.text-green-700]="!isAdmin">
                {{ userRole }}
              </span>
            </div>
            <button (click)="logout()" 
                    class="bg-gray-100 text-gray-700 px-4 py-2 rounded-lg text-sm hover:bg-gray-200 transition-colors">
              Déconnexion
            </button>
          </div>
        </div>
        
        <!-- Mobile menu -->
        <div *ngIf="mobileMenuOpen" class="md:hidden py-4 border-t">
          <div class="flex flex-col space-y-3">
            <a routerLink="/dashboard" (click)="mobileMenuOpen = false"
               class="text-gray-700 hover:text-primary font-medium py-2">Dashboard</a>
            
            <!-- Admin Mobile Links -->
            <ng-container *ngIf="isAdmin">
              <a routerLink="/admin/users" (click)="mobileMenuOpen = false"
                 class="text-gray-700 hover:text-primary font-medium py-2">Utilisateurs</a>
              <a routerLink="/admin/farms" (click)="mobileMenuOpen = false"
                 class="text-gray-700 hover:text-primary font-medium py-2">Toutes les Fermes</a>
            </ng-container>
            
            <a *ngIf="!isAdmin" routerLink="/farms" (click)="mobileMenuOpen = false"
               class="text-gray-700 hover:text-primary font-medium py-2">Mes Fermes</a>
            <a routerLink="/irrigation" (click)="mobileMenuOpen = false"
               class="text-gray-700 hover:text-primary font-medium py-2">Irrigation</a>
            <a routerLink="/stats" (click)="mobileMenuOpen = false"
               class="text-gray-700 hover:text-primary font-medium py-2">Stats</a>
            <a routerLink="/weather" (click)="mobileMenuOpen = false"
               class="text-gray-700 hover:text-primary font-medium py-2">Météo</a>
            <a routerLink="/alerts" (click)="mobileMenuOpen = false"
               class="text-gray-700 hover:text-primary font-medium py-2">Alertes</a>
            <div class="pt-3 border-t">
              <div *ngIf="user$ | async as user" class="flex items-center gap-2 mb-2">
                <span class="text-gray-600 text-sm">{{ user.firstName }}</span>
                <span class="text-xs px-2 py-1 rounded-full"
                      [class.bg-purple-100]="isAdmin"
                      [class.text-purple-700]="isAdmin"
                      [class.bg-green-100]="!isAdmin"
                      [class.text-green-700]="!isAdmin">
                  {{ userRole }}
                </span>
              </div>
              <button (click)="logout()" 
                      class="bg-gray-100 text-gray-700 px-4 py-2 rounded-lg text-sm hover:bg-gray-200 w-full">
                Déconnexion
              </button>
            </div>
          </div>
        </div>
      </div>
    </nav>
  `
})
export class NavbarComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  
  user$ = this.authService.currentUser$;
  mobileMenuOpen = false;

  get isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  get userRole(): string {
    return this.authService.getPrimaryRole();
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
