import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Alert, AlertSummary, AlertPreference } from '../models/alert.model';

const API_URL = 'http://localhost:8080/api/alerts';

@Injectable({
  providedIn: 'root'
})
export class AlertService {

  constructor(private http: HttpClient) { }

  // ========== ALERTS ==========

  // Récupérer les alertes d'une ferme
  getAlertsByFarm(farmId: number): Observable<any> {
    return this.http.get<any>(`${API_URL}/farm/${farmId}`);
  }

  // Récupérer les alertes non lues d'une ferme
  getUnreadAlerts(farmId: number): Observable<Alert[]> {
    return this.http.get<Alert[]>(`${API_URL}/farm/${farmId}/unread`);
  }

  // Récupérer les alertes actives d'une ferme
  getActiveAlerts(farmId: number): Observable<Alert[]> {
    return this.http.get<Alert[]>(`${API_URL}/farm/${farmId}/active`);
  }

  // Récupérer les alertes critiques
  getCriticalAlerts(farmId: number): Observable<Alert[]> {
    return this.http.get<Alert[]>(`${API_URL}/farm/${farmId}/critical`);
  }

  // Récupérer le résumé des alertes
  getAlertSummary(farmId: number): Observable<AlertSummary> {
    return this.http.get<AlertSummary>(`${API_URL}/farm/${farmId}/summary`);
  }

  // Récupérer une alerte par ID
  getAlertById(id: number): Observable<Alert> {
    return this.http.get<Alert>(`${API_URL}/${id}`);
  }

  // Marquer une alerte comme lue
  markAsRead(id: number): Observable<Alert> {
    return this.http.post<Alert>(`${API_URL}/${id}/read`, {});
  }

  // Marquer toutes les alertes comme lues
  markAllAsRead(farmId: number): Observable<void> {
    return this.http.post<void>(`${API_URL}/farm/${farmId}/read-all`, {});
  }

  // Acquitter une alerte
  acknowledgeAlert(id: number, userId: number): Observable<Alert> {
    const params = new HttpParams().set('userId', userId);
    return this.http.post<Alert>(`${API_URL}/${id}/acknowledge`, null, { params });
  }

  // Résoudre une alerte
  resolveAlert(id: number, resolution: string): Observable<Alert> {
    const params = new HttpParams().set('resolution', resolution);
    return this.http.post<Alert>(`${API_URL}/${id}/resolve`, null, { params });
  }

  // Supprimer une alerte
  deleteAlert(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/${id}`);
  }

  // ========== PREFERENCES ==========

  // Récupérer les préférences d'une ferme
  getPreferences(farmId: number): Observable<AlertPreference[]> {
    return this.http.get<AlertPreference[]>(`${API_URL}/preferences/farm/${farmId}`);
  }

  // Créer ou mettre à jour une préférence
  savePreference(farmId: number, alertType: string, preference: any): Observable<AlertPreference> {
    return this.http.post<AlertPreference>(`${API_URL}/preferences/farm/${farmId}/${alertType}`, preference);
  }

  // Activer les notifications email
  enableEmailNotifications(farmId: number): Observable<void> {
    return this.http.post<void>(`${API_URL}/preferences/farm/${farmId}/email/enable`, {});
  }

  // Désactiver les notifications email
  disableEmailNotifications(farmId: number): Observable<void> {
    return this.http.post<void>(`${API_URL}/preferences/farm/${farmId}/email/disable`, {});
  }

  // ========== GENERATION (pour tests) ==========

  // Générer des alertes pour une parcelle
  checkParcelAlerts(parcelId: number, farmId: number, soilMoisture?: number, temperature?: number): Observable<Alert[]> {
    let params = new HttpParams()
      .set('parcelId', parcelId)
      .set('farmId', farmId);
    
    if (soilMoisture !== undefined) {
      params = params.set('soilMoisture', soilMoisture);
    }
    if (temperature !== undefined) {
      params = params.set('temperature', temperature);
    }

    return this.http.post<Alert[]>(`${API_URL}/check`, null, { params });
  }
}
