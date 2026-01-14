import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, CommonModule],
  template: `
    <div class="min-h-screen flex justify-center items-center p-4">
      <div class="glass-card w-full max-w-md">
        <h2 class="text-3xl font-bold text-center mb-8 text-primary-dark">Create Account</h2>
        
        <form [formGroup]="registerForm" (ngSubmit)="onSubmit()" class="flex flex-col gap-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block mb-2 font-medium text-text-main">First Name</label>
              <input type="text" formControlName="firstName" class="w-full p-3 rounded-lg border border-white/50 bg-white/80 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-all placeholder-gray-400" placeholder="Your first name">
            </div>
            <div>
              <label class="block mb-2 font-medium text-text-main">Last Name</label>
              <input type="text" formControlName="lastName" class="w-full p-3 rounded-lg border border-white/50 bg-white/80 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-all placeholder-gray-400" placeholder="Your last name">
            </div>
          </div>

          <div>
            <label class="block mb-2 font-medium text-text-main">Email</label>
            <input type="email" formControlName="email" class="w-full p-3 rounded-lg border border-white/50 bg-white/80 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-all placeholder-gray-400" placeholder="Enter your email">
          </div>
          
          <div>
            <label class="block mb-2 font-medium text-text-main">Password</label>
            <input type="password" formControlName="password" class="w-full p-3 rounded-lg border border-white/50 bg-white/80 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-all placeholder-gray-400" placeholder="Create a password">
          </div>

          <div>
            <label class="block mb-2 font-medium text-text-main">Phone Number (optional)</label>
            <input type="tel" formControlName="phoneNumber" class="w-full p-3 rounded-lg border border-white/50 bg-white/80 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-all placeholder-gray-400" placeholder="Your phone number">
          </div>

           <div>
            <label class="block mb-2 font-medium text-text-main">Role</label>
            <select formControlName="role" class="w-full p-3 rounded-lg border border-white/50 bg-white/80 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent transition-all">
              <option value="AGRICULTEUR">Agriculteur</option>
              <option value="ADMIN">Admin</option>
               <option value="ONG">ONG</option>
            </select>
          </div>

          <div *ngIf="errorMessage" class="bg-red-50 text-red-600 p-3 rounded-lg border border-red-200 text-sm">
            {{ errorMessage }}
          </div>
          
          <button type="submit" class="w-full bg-primary text-white py-3 rounded-lg font-bold hover:bg-primary-dark transition-all shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed" [disabled]="registerForm.invalid || isLoading">
            {{ isLoading ? 'Creating account...' : 'Sign Up' }}
          </button>
        </form>
        
        <p class="mt-6 text-center text-text-main">
          Already have an account? <a routerLink="/login" class="text-primary-dark font-bold hover:underline">Login here</a>
        </p>
      </div>
    </div>
  `,
  styles: []
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  registerForm = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    phoneNumber: [''],
    role: ['AGRICULTEUR', Validators.required]
  });

  errorMessage = '';
  isLoading = false;

  onSubmit() {
    if (this.registerForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      // Format the request to match the backend RegisterRequest DTO
      const request = {
        firstName: this.registerForm.value.firstName,
        lastName: this.registerForm.value.lastName,
        email: this.registerForm.value.email,
        password: this.registerForm.value.password,
        phoneNumber: this.registerForm.value.phoneNumber || null,
        roles: [this.registerForm.value.role]
      };

      this.authService.register(request).subscribe({
        next: () => {
          this.router.navigate(['/login']);
        },
        error: (err) => {
          this.isLoading = false;
          console.error('Registration error:', err);

          // Better error handling
          if (err.status === 409 || err.error?.message?.includes('existe déjà')) {
            this.errorMessage = 'This email is already registered.';
          } else if (err.status === 400) {
            this.errorMessage = err.error?.message || 'Invalid form data. Please check your inputs.';
          } else {
            this.errorMessage = 'Registration failed. Please try again.';
          }
        }
      });
    }
  }
}
