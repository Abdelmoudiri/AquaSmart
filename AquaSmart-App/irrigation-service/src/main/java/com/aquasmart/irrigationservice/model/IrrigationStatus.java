package com.aquasmart.irrigationservice.model;

/**
 * Status of an irrigation schedule
 */
public enum IrrigationStatus {
    SCHEDULED,      // Programmée
    IN_PROGRESS,    // En cours
    COMPLETED,      // Terminée
    CANCELLED,      // Annulée
    SKIPPED,        // Ignorée (ex: pluie)
    FAILED          // Échouée
}
