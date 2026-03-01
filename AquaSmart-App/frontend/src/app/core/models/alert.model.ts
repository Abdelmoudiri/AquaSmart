// ========== ALERT MODELS ==========
// Modèles simples pour les alertes

export interface Alert {
  id: number;
  userId: number;
  farmId?: number;
  parcelId?: number;
  type: string;
  severity: 'INFO' | 'WARNING' | 'CRITICAL' | 'EMERGENCY';
  status: 'NEW' | 'READ' | 'ACKNOWLEDGED' | 'RESOLVED' | 'DISMISSED';
  title: string;
  message?: string;
  source?: string;
  triggerValue?: number;
  thresholdValue?: number;
  recommendedAction?: string;
  createdAt: string;
  readAt?: string;
  resolvedAt?: string;
}

export interface AlertSummary {
  userId?: number;
  farmId?: number;
  totalAlerts: number;
  newAlerts: number;
  readAlerts: number;
  acknowledgedAlerts: number;
  resolvedAlerts: number;
  infoCount: number;
  warningCount: number;
  criticalCount: number;
  emergencyCount: number;
  // Alias pour faciliter l'utilisation
  unreadCount?: number;
}

export interface AlertPreference {
  id: number;
  userId: number;
  email?: string;
  phoneNumber?: string;
  emailEnabled: boolean;
  smsEnabled: boolean;
  pushEnabled: boolean;
  inAppEnabled: boolean;
  emailMinSeverity: string;
  smsMinSeverity: string;
  quietHoursStart?: number;
  quietHoursEnd?: number;
}
