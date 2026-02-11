package com.aquasmart.irrigationservice.model;

/**
 * Source that triggered the irrigation
 */
public enum IrrigationSource {
    MANUAL,         // Déclenché manuellement par l'utilisateur
    SCHEDULED,      // Planification automatique
    SENSOR_BASED,   // Basé sur les données des capteurs
    WEATHER_BASED,  // Basé sur les prévisions météo
    AI_RECOMMENDED  // Recommandé par l'IA
}
