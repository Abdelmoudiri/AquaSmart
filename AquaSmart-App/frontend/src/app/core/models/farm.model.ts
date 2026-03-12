// ========== FARM MODELS ==========
// Modèles simples pour les fermes, parcelles et cultures

export interface Farm {
  id: number;
  name: string;
  location: string;
  latitude: number;
  longitude: number;
  totalArea: number;
  ownerId: number;
  active?: boolean;
  description?: string;
  createdAt?: string;
  parcels?: Parcel[];
}

export interface Parcel {
  id: number;
  farmId: number;
  name: string;
  area: number;
  soilType: string;
  irrigationType: string;
  status: string;
  active?: boolean;
  latitude?: number;
  longitude?: number;
  currentMoisture?: number;
  optimalMoistureMin?: number;
  optimalMoistureMax?: number;
  description?: string;
  crops?: Crop[];
}

export interface Crop {
  id: number;
  parcelId: number;
  name: string;
  cropType: string;
  growthStage: string;
  plantingDate: string;
  expectedHarvestDate?: string;
  waterRequirement: number;
  notes?: string;
}

// Types pour les formulaires
export interface FarmRequest {
  name: string;
  location: string;
  latitude?: number;
  longitude?: number;
  totalArea: number;
  ownerId?: number;
  description?: string;
}

export interface ParcelRequest {
  farmId: number;
  name: string;
  area: number;
  soilType: string;
  irrigationType: string;
  description?: string;
  latitude?: number;
  longitude?: number;
}
