import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen">
      <nav class="glass-card rounded-t-none rounded-b-2xl flex justify-between items-center px-8 py-4 mb-8 sticky top-0 z-50">
        <div class="text-2xl font-extrabold text-primary-dark">AquaSmart</div>
        <div class="flex items-center gap-4 text-text-main font-medium">
          <span *ngIf="user$ | async as user">Welcome, {{ user.username }}</span>
          <button (click)="logout()" class="border border-primary text-primary px-4 py-1.5 rounded-lg text-sm font-semibold hover:bg-primary hover:text-white transition-all">Logout</button>
        </div>
      </nav>

      <div class="max-w-7xl mx-auto px-4">
        <div class="glass-card mt-8 p-8 animate-fade-in">
          <h2 class="text-2xl font-bold text-text-main mb-4">Dashboard Overview</h2>
          <p class="text-text-main opacity-80 mb-8">Welcome to your AquaSmart dashboard. Here you can manage your water consumption and view analytics.</p>
          
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            <div class="bg-white/50 p-6 rounded-xl text-center shadow-sm hover:shadow-md transition-all">
              <h3 class="text-base text-text-main opacity-80 mb-2">Water Quality</h3>
              <p class="text-3xl font-bold text-primary-dark">Good</p>
            </div>
             <div class="bg-white/50 p-6 rounded-xl text-center shadow-sm hover:shadow-md transition-all">
              <h3 class="text-base text-text-main opacity-80 mb-2">Usage (Litres)</h3>
              <p class="text-3xl font-bold text-primary-dark">1,240</p>
            </div>
             <div class="bg-white/50 p-6 rounded-xl text-center shadow-sm hover:shadow-md transition-all">
              <h3 class="text-base text-text-main opacity-80 mb-2">Status</h3>
              <p class="text-3xl font-bold text-teal-600">Active</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class DashboardComponent {
  authService = inject(AuthService);
  router = inject(Router);

  user$ = this.authService.currentUser$;

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
