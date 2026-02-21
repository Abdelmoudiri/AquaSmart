package com.aquasmart.alertservice.model;

/**
 * Type of alert
 */
public enum AlertType {
    // Sensor alerts
    SOIL_MOISTURE_LOW,
    SOIL_MOISTURE_HIGH,
    TEMPERATURE_LOW,
    TEMPERATURE_HIGH,
    HUMIDITY_LOW,
    HUMIDITY_HIGH,
    SENSOR_OFFLINE,
    SENSOR_BATTERY_LOW,
    
    // Irrigation alerts
    IRRIGATION_FAILED,
    IRRIGATION_SKIPPED,
    IRRIGATION_SCHEDULED,
    WATER_USAGE_HIGH,
    
    // Weather alerts
    FROST_WARNING,
    HEAT_WAVE,
    HEAVY_RAIN,
    STORM_WARNING,
    DROUGHT_WARNING,
    
    // System alerts
    SYSTEM_ERROR,
    MAINTENANCE_REQUIRED,
    
    // Custom alerts
    CUSTOM
}
