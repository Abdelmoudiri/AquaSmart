import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { 
  IrrigationSchedule, 
  IrrigationEvent, 
  WaterUsageStats,
  IrrigationRecommendation,
  DailyWaterUsage,
  WaterUsageChartData,
  ParcelWaterUsage
} from '../models/irrigation.model';

const API_URL = 'http://localhost:8080/api/irrigation';

@Injectable({
  providedIn: 'root'
})
export class IrrigationService {

  constructor(private http: HttpClient) { }

  // ========== SCHEDULES (Plannings) ==========

  // Récupérer les plannings d'une ferme
  getSchedulesByFarm(farmId: number): Observable<IrrigationSchedule[]> {
    return this.http.get<IrrigationSchedule[]>(`${API_URL}/schedules/farm/${farmId}`);
  }

  // Récupérer les plannings d'une parcelle
  getSchedulesByParcel(parcelId: number): Observable<IrrigationSchedule[]> {
    return this.http.get<IrrigationSchedule[]>(`${API_URL}/schedules/parcel/${parcelId}`);
  }

  // Créer un planning
  createSchedule(schedule: any): Observable<IrrigationSchedule> {
    return this.http.post<IrrigationSchedule>(`${API_URL}/schedules`, schedule);
  }

  // Modifier un planning
  updateSchedule(id: number, schedule: any): Observable<IrrigationSchedule> {
    return this.http.put<IrrigationSchedule>(`${API_URL}/schedules/${id}`, schedule);
  }

  // Supprimer un planning
  deleteSchedule(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/schedules/${id}`);
  }

  // Activer/Désactiver un planning
  activateSchedule(id: number): Observable<IrrigationSchedule> {
    return this.http.post<IrrigationSchedule>(`${API_URL}/schedules/${id}/activate`, {});
  }

  deactivateSchedule(id: number): Observable<IrrigationSchedule> {
    return this.http.post<IrrigationSchedule>(`${API_URL}/schedules/${id}/deactivate`, {});
  }

  // ========== EVENTS (Événements d'irrigation) ==========

  // Récupérer les événements d'une ferme
  getEventsByFarm(farmId: number): Observable<any> {
    return this.http.get<any>(`${API_URL}/events/farm/${farmId}`);
  }

  // Récupérer les événements en cours
  getInProgressEvents(): Observable<IrrigationEvent[]> {
    return this.http.get<IrrigationEvent[]>(`${API_URL}/events/in-progress`);
  }

  // Déclencher une irrigation manuelle
  triggerManualIrrigation(parcelId: number, farmId: number, duration: number, waterAmount: number): Observable<IrrigationEvent> {
    const params = new HttpParams()
      .set('parcelId', parcelId)
      .set('farmId', farmId)
      .set('durationMinutes', duration)
      .set('waterAmount', waterAmount);
    
    return this.http.post<IrrigationEvent>(`${API_URL}/events/manual`, null, { params });
  }

  // Compléter un événement
  completeEvent(id: number, actualWaterAmount: number): Observable<IrrigationEvent> {
    const params = new HttpParams()
      .set('actualWaterAmount', actualWaterAmount);
    
    return this.http.post<IrrigationEvent>(`${API_URL}/events/${id}/complete`, null, { params });
  }

  // Annuler un événement
  cancelEvent(id: number, reason?: string): Observable<IrrigationEvent> {
    let params = new HttpParams();
    if (reason) {
      params = params.set('reason', reason);
    }
    return this.http.post<IrrigationEvent>(`${API_URL}/events/${id}/cancel`, null, { params });
  }

  // ========== SMART IRRIGATION ==========

  // Obtenir une recommandation intelligente
  getRecommendation(parcelId: number, farmId: number, lat: number, lon: number, soilMoisture?: number): Observable<IrrigationRecommendation> {
    let params = new HttpParams()
      .set('parcelId', parcelId)
      .set('farmId', farmId)
      .set('latitude', lat)
      .set('longitude', lon);
    
    if (soilMoisture !== undefined) {
      params = params.set('soilMoisture', soilMoisture);
    }

    return this.http.get<IrrigationRecommendation>(`${API_URL}/recommendation`, { params });
  }

  // ========== STATISTIQUES ==========

  // Obtenir les stats d'une ferme
  getFarmStats(farmId: number, startDate: string, endDate: string): Observable<WaterUsageStats> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate);
    
    return this.http.get<WaterUsageStats>(`${API_URL}/stats/farm/${farmId}`, { params });
  }

  // Obtenir les stats mensuelles
  getMonthlyStats(farmId: number, year: number, month: number): Observable<WaterUsageStats> {
    const params = new HttpParams()
      .set('year', year)
      .set('month', month);
    
    return this.http.get<WaterUsageStats>(`${API_URL}/stats/farm/${farmId}/monthly`, { params });
  }

  // ========== DONNÉES POUR GRAPHIQUES ==========

  // Générer des données quotidiennes pour les graphiques (mock pour démo)
  getDailyChartData(farmId: number, days: number = 7): Observable<WaterUsageChartData> {
    const labels: string[] = [];
    const waterUsed: number[] = [];
    const waterSaved: number[] = [];
    const dailyData: DailyWaterUsage[] = [];

    const today = new Date();
    const dayNames = ['Dim', 'Lun', 'Mar', 'Mer', 'Jeu', 'Ven', 'Sam'];

    for (let i = days - 1; i >= 0; i--) {
      const date = new Date(today);
      date.setDate(date.getDate() - i);
      
      const dayLabel = dayNames[date.getDay()];
      const dateStr = date.toISOString().split('T')[0];
      
      // Génération de données réalistes avec variation
      const baseUsage = 800 + Math.random() * 400; // 800-1200L par jour
      const saved = baseUsage * (0.15 + Math.random() * 0.25); // 15-40% économisé
      const events = Math.floor(2 + Math.random() * 4);

      labels.push(dayLabel);
      waterUsed.push(Math.round(baseUsage));
      waterSaved.push(Math.round(saved));
      
      dailyData.push({
        date: dateStr,
        waterUsed: Math.round(baseUsage),
        waterSaved: Math.round(saved),
        eventsCount: events
      });
    }

    return of({ labels, waterUsed, waterSaved, dailyData });
  }

  // Données mensuelles pour graphique
  getMonthlyChartData(farmId: number, months: number = 6): Observable<WaterUsageChartData> {
    const labels: string[] = [];
    const waterUsed: number[] = [];
    const waterSaved: number[] = [];

    const monthNames = ['Jan', 'Fév', 'Mar', 'Avr', 'Mai', 'Juin', 'Juil', 'Août', 'Sep', 'Oct', 'Nov', 'Déc'];
    const today = new Date();

    for (let i = months - 1; i >= 0; i--) {
      const date = new Date(today);
      date.setMonth(date.getMonth() - i);
      
      labels.push(monthNames[date.getMonth()]);
      
      // Données mensuelles avec variation saisonnière
      const seasonFactor = 1 + Math.sin((date.getMonth() - 3) * Math.PI / 6) * 0.3; // Plus d'eau en été
      const baseUsage = 25000 * seasonFactor + Math.random() * 5000;
      const saved = baseUsage * (0.2 + Math.random() * 0.15);

      waterUsed.push(Math.round(baseUsage));
      waterSaved.push(Math.round(saved));
    }

    return of({ labels, waterUsed, waterSaved });
  }

  // Répartition par parcelle
  getParcelDistribution(farmId: number): Observable<ParcelWaterUsage[]> {
    // Mock data - à remplacer par un vrai appel API
    const parcels: ParcelWaterUsage[] = [
      { parcelId: 1, parcelName: 'Parcelle Nord', totalWaterUsed: 4500, percentage: 35 },
      { parcelId: 2, parcelName: 'Parcelle Sud', totalWaterUsed: 3200, percentage: 25 },
      { parcelId: 3, parcelName: 'Verger Est', totalWaterUsed: 2800, percentage: 22 },
      { parcelId: 4, parcelName: 'Serre Centrale', totalWaterUsed: 2300, percentage: 18 }
    ];
    
    return of(parcels);
  }

  // Stats résumées pour dashboard
  getDashboardStats(farmId: number): Observable<{
    totalWaterUsed: number;
    totalWaterSaved: number;
    savingsPercentage: number;
    totalEvents: number;
    trend: number;
    weeklyData: number[];
  }> {
    const weeklyData = Array.from({ length: 7 }, () => 600 + Math.random() * 600);
    const totalUsed = weeklyData.reduce((a, b) => a + b, 0);
    const totalSaved = totalUsed * 0.28;
    
    return of({
      totalWaterUsed: Math.round(totalUsed),
      totalWaterSaved: Math.round(totalSaved),
      savingsPercentage: 28,
      totalEvents: Math.floor(20 + Math.random() * 15),
      trend: Math.round(-5 + Math.random() * 15), // -5% à +10%
      weeklyData: weeklyData.map(v => Math.round(v))
    });
  }
}
