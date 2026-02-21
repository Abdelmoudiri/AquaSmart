package com.aquasmart.alertservice.model;

/**
 * Severity level of an alert
 */
public enum AlertSeverity {
    INFO,       // Informational
    WARNING,    // Warning - attention needed
    CRITICAL,   // Critical - immediate action required
    EMERGENCY   // Emergency - urgent intervention
}
