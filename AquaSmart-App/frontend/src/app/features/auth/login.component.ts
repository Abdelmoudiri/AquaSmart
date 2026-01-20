import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, CommonModule],
  template: `
    <div class="min-h-screen flex justify-center items-center p-4">
      <div class="glass-card w-full max-w-md">
        <h2 class="text-3xl font-bold text-center mb-8 text-primary-dark">Login to AquaSmart</h2>
        
        <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="flex flex-col gap-4">
          <div>
            <label class="block mb-2 font-medium text-text-main">Email</label>
            <input type="email" formControlName="email" class="w-full p-3 rounded-lg border border-white/50 bg-white/80 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-all placeholder-gray-400" placeholder="Enter your email">
          </div>
          
          <div>
            <label class="block mb-2 font-medium text-text-main">Password</label>
            <input type="password" formControlName="password" class="w-full p-3 rounded-lg border border-white/50 bg-white/80 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-all placeholder-gray-400" placeholder="Enter your password">
          </div>

          <div *ngIf="errorMessage" class="bg-red-50 text-red-600 p-3 rounded-lg border border-red-200 text-sm">
            {{ errorMessage }}
          </div>
          
          <button type="submit" class="w-full bg-primary text-white py-3 rounded-lg font-bold hover:bg-primary-dark transition-all shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed" [disabled]="loginForm.invalid">Sign In</button>
        </form>
        
        <p class="mt-6 text-center text-text-main">
          Don't have an account? <a routerLink="/register" class="text-primary-dark font-bold hover:underline">Register here</a>
        </p>
      </div>
    </div>
  `,
  styles: []
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required]
  });

  errorMessage = '';

  onSubmit() {
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value).subscribe({
        next: () => this.router.navigate(['/dashboard']),
        error: (err) => {
          console.error(err);
          this.errorMessage = 'Invalid credentials. Please try again.';
        }
      });
    }
  }
}
