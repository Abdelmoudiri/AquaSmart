import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { LoginComponent } from './features/auth/login.component';
import { RegisterComponent } from './features/auth/register.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { FarmListComponent } from './features/farms/farm-list/farm-list.component';
import { FarmDetailComponent } from './features/farms/farm-detail/farm-detail.component';
import { FarmFormComponent } from './features/farms/farm-form/farm-form.component';
import { ParcelFormComponent } from './features/farms/parcel-form/parcel-form.component';
import { IrrigationComponent } from './features/irrigation/irrigation.component';
import { AlertListComponent } from './features/alerts/alert-list/alert-list.component';
import { WeatherComponent } from './features/weather/weather.component';
import { StatsComponent } from './features/stats/stats.component';
import { AdminUsersComponent } from './features/admin/admin-users/admin-users.component';
import { AdminFarmsComponent } from './features/admin/admin-farms/admin-farms.component';

export const routes: Routes = [
    // Public routes
    { path: '', component: HomeComponent },
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    
    // Protected routes (Add AuthGuard later)
    { path: 'dashboard', component: DashboardComponent },
    
    // Admin routes
    { path: 'admin/users', component: AdminUsersComponent },
    { path: 'admin/farms', component: AdminFarmsComponent },
    
    // Farms - Routes spécifiques en premier
    { path: 'farms', component: FarmListComponent },
    { path: 'farms/new', component: FarmFormComponent },
    
    // Parcels - AVANT farms/:id pour éviter les conflits
    { path: 'farms/:farmId/parcels/new', component: ParcelFormComponent },
    { path: 'farms/:farmId/parcels/:parcelId', component: ParcelFormComponent },
    { path: 'farms/:farmId/edit', component: FarmFormComponent },
    
    // Farm detail - APRÈS les routes parcels
    { path: 'farms/:farmId', component: FarmDetailComponent },
    
    // Irrigation
    { path: 'irrigation', component: IrrigationComponent },
    
    // Alerts
    { path: 'alerts', component: AlertListComponent },
    
    // Weather
    { path: 'weather', component: WeatherComponent },
    
    // Stats
    { path: 'stats', component: StatsComponent },
    
    // Fallback
    { path: '**', redirectTo: '' }
];
