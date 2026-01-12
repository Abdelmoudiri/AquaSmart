import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="h-screen flex justify-center items-center text-center p-4">
      <div class="glass-card max-w-2xl w-full p-12 animate-fade-in">
        <h1 class="text-5xl font-bold mb-4 text-text-main">
          Welcome to <span class="bg-gradient-to-r from-primary-dark to-accent bg-clip-text text-transparent">AquaSmart</span>
        </h1>
        <p class="text-xl text-text-main mb-8 opacity-90">Intelligent Water Management for a Sustainable Future</p>
        
        <div class="flex gap-4 justify-center">
          <a routerLink="/login" class="px-6 py-3 bg-primary text-white rounded-lg font-semibold shadow-lg hover:bg-primary-dark hover:-translate-y-0.5 transition-all">Login</a>
          <a routerLink="/register" class="px-6 py-3 border-2 border-primary text-primary rounded-lg font-semibold hover:bg-primary hover:text-white transition-all">Register</a>
        </div>
      </div>
    </div>
  `,
  styles: [] // Styles handled by Tailwind
})
export class HomeComponent { }
