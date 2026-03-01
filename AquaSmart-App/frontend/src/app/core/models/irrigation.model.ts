// ========== IRRIGATION MODELS ==========
// Modèles simples pour l'irrigation

export interface IrrigationSchedule {
  id: number;
  parcelId: number;
  farmId: number;
  name: string;
  description?: string;
  irrigationType: string;
  startTime: string;
  durationMinutes: number;
  waterAmountPerSquareMeter: number;
  waterAmount?: number; // Alias for compatibility
  frequency?: string; // Additional field
  activeDays?: string;
  active: boolean;
  weatherAware: boolean;
  sensorAware: boolean;
  priority: string;
  createdAt?: string;
}

export interface IrrigationEvent {
  id: number;
  scheduleId?: number;
  scheduleName?: string;
  parcelId: number;
  farmId: number;
  source: string;
  status: string;
  scheduledStartTime: string;
  scheduledEndTime: string;
  actualStartTime?: string;
  actualEndTime?: string;
  plannedWaterAmount: number;
  actualWaterAmount?: number;
  waterAmount?: number; // Alias for compatibility
  durationMinutes?: number; // Additional field
  soilMoistureBefore?: number;
  soilMoistureAfter?: number;
  waterSaved?: number;
  efficiencyScore?: number;
}

export interface WaterUsageStats {
  farmId: number;
  parcelId?: number;
  startDate: string;
  endDate: string;
  totalWaterUsed: number;
  totalWaterSaved: number;
  savingsPercentage: number;
  totalCost: number;
  totalSavings: number;
  averageDailyUsage: number;
  totalEvents: number;
}

export interface IrrigationRecommendation {
  parcelId: number;
  farmId: number;
  shouldIrrigate: boolean;
  recommendedDurationMinutes: number;
  recommendedDuration?: number; // Alias for compatibility
  recommendedWaterAmount: number;
  optimalStartTime: string;
  confidenceScore: number;
  reasons: string[];
  reason?: string; // Alias for single reason display
  warnings: string[];
  conditions: {
    soilMoisture?: number;
    temperature?: number;
    humidity?: number;
    windSpeed?: number;
    rainProbability?: number;
    weatherDescription?: string;
  };
}

// ========== CHART DATA MODELS ==========

export interface DailyWaterUsage {
  date: string;
  waterUsed: number;
  waterSaved: number;
  eventsCount: number;
}

export interface WeeklyWaterUsage {
  weekStart: string;
  weekEnd: string;
  totalWaterUsed: number;
  totalWaterSaved: number;
  averageDailyUsage: number;
  eventsCount: number;
}

export interface ParcelWaterUsage {
  parcelId: number;
  parcelName: string;
  totalWaterUsed: number;
  percentage: number;
}

export interface WaterUsageChartData {
  labels: string[];
  waterUsed: number[];
  waterSaved: number[];
  dailyData?: DailyWaterUsage[];
}
