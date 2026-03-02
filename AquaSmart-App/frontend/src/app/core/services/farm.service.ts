import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Farm, Parcel, Crop, FarmRequest, ParcelRequest } from '../models/farm.model';

// URL de base du Gateway
const API_URL = 'http://localhost:8080/api';

@Injectable({
  providedIn: 'root'
})
export class FarmService {

  constructor(private http: HttpClient) { }

  // ========== FARMS ==========
  
  // Récupérer toutes les fermes
  getAllFarms(): Observable<Farm[]> {
    return this.http.get<Farm[]>(`${API_URL}/farms`);
  }
  
  // Récupérer les fermes de l'utilisateur connecté
  getMyFarms(): Observable<Farm[]> {
    return this.http.get<Farm[]>(`${API_URL}/farms/my-farms`);
  }

  // Alias pour compatibilité (utilise getMyFarms)
  getFarmsByOwner(ownerId?: number): Observable<Farm[]> {
    return this.getMyFarms();
  }

  // Récupérer une ferme par ID
  getFarmById(id: number): Observable<Farm> {
    return this.http.get<Farm>(`${API_URL}/farms/${id}`);
  }

  // Créer une nouvelle ferme
  createFarm(farm: FarmRequest): Observable<Farm> {
    return this.http.post<Farm>(`${API_URL}/farms`, farm);
  }

  // Modifier une ferme
  updateFarm(id: number, farm: FarmRequest): Observable<Farm> {
    return this.http.put<Farm>(`${API_URL}/farms/${id}`, farm);
  }

  // Supprimer une ferme
  deleteFarm(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/farms/${id}`);
  }

  // ========== PARCELS ==========

  // Récupérer les parcelles d'une ferme
  getParcelsByFarm(farmId: number): Observable<Parcel[]> {
    return this.http.get<Parcel[]>(`${API_URL}/farms/${farmId}/parcels`);
  }

  // Récupérer une parcelle par ID
  getParcelById(id: number, farmId?: number): Observable<Parcel> {
    // Utiliser farmId=1 par défaut si non fourni
    const fId = farmId || 1;
    return this.http.get<Parcel>(`${API_URL}/farms/${fId}/parcels/${id}`);
  }

  // Créer une nouvelle parcelle
  createParcel(parcel: ParcelRequest): Observable<Parcel> {
    return this.http.post<Parcel>(`${API_URL}/farms/${parcel.farmId}/parcels`, parcel);
  }

  // Modifier une parcelle
  updateParcel(id: number, parcel: ParcelRequest): Observable<Parcel> {
    return this.http.put<Parcel>(`${API_URL}/farms/${parcel.farmId}/parcels/${id}`, parcel);
  }

  // Supprimer une parcelle
  deleteParcel(id: number, farmId?: number): Observable<void> {
    const fId = farmId || 1;
    return this.http.delete<void>(`${API_URL}/farms/${fId}/parcels/${id}`);
  }

  // ========== CROPS ==========

  // Récupérer les cultures d'une parcelle
  getCropsByParcel(parcelId: number): Observable<Crop[]> {
    return this.http.get<Crop[]>(`${API_URL}/parcels/${parcelId}/crops`);
  }

  // Créer une nouvelle culture
  createCrop(crop: any): Observable<Crop> {
    return this.http.post<Crop>(`${API_URL}/crops`, crop);
  }

  // Supprimer une culture
  deleteCrop(id: number): Observable<void> {
    return this.http.delete<void>(`${API_URL}/crops/${id}`);
  }
}
