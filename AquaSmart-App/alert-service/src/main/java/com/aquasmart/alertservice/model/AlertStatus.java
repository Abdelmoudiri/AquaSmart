package com.aquasmart.alertservice.model;

/**
 * Status of an alert
 */
public enum AlertStatus {
    NEW,            // New alert, not yet seen
    READ,           // Alert has been read
    ACKNOWLEDGED,   // Alert has been acknowledged
    RESOLVED,       // Alert has been resolved
    DISMISSED       // Alert was dismissed without action
}
