package com.aquasmart.alertservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Alert entity - represents an alert in the system
 */
@Entity
@Table(name = "alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * User who should receive this alert
     */
    @Column(nullable = false)
    private Long userId;
    
    /**
     * Farm related to this alert (optional)
     */
    private Long farmId;
    
    /**
     * Parcel related to this alert (optional)
     */
    private Long parcelId;
    
    /**
     * Type of alert
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType type;
    
    /**
     * Severity level
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AlertSeverity severity = AlertSeverity.INFO;
    
    /**
     * Current status
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private AlertStatus status = AlertStatus.NEW;
    
    /**
     * Alert title
     */
    @Column(nullable = false)
    private String title;
    
    /**
     * Detailed message
     */
    @Column(columnDefinition = "TEXT")
    private String message;
    
    /**
     * Source of the alert (sensor ID, service name, etc.)
     */
    private String source;
    
    /**
     * Value that triggered the alert (if applicable)
     */
    private Double triggerValue;
    
    /**
     * Threshold value (if applicable)
     */
    private Double thresholdValue;
    
    /**
     * Recommended action
     */
    private String recommendedAction;
    
    /**
     * Metadata as JSON
     */
    @Column(columnDefinition = "TEXT")
    private String metadata;
    
    /**
     * When the alert was created
     */
    private LocalDateTime createdAt;
    
    /**
     * When the alert was read
     */
    private LocalDateTime readAt;
    
    /**
     * When the alert was acknowledged
     */
    private LocalDateTime acknowledgedAt;
    
    /**
     * When the alert was resolved
     */
    private LocalDateTime resolvedAt;
    
    /**
     * Who resolved the alert
     */
    private Long resolvedBy;
    
    /**
     * Resolution notes
     */
    private String resolutionNotes;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
