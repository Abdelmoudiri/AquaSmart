package com.aquasmart.alertservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * User alert preferences
 */
@Entity
@Table(name = "alert_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertPreference {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private Long userId;
    
    /**
     * Email for notifications
     */
    private String email;
    
    /**
     * Phone number for SMS
     */
    private String phoneNumber;
    
    /**
     * Enable email notifications
     */
    @Builder.Default
    private Boolean emailEnabled = true;
    
    /**
     * Enable SMS notifications
     */
    @Builder.Default
    private Boolean smsEnabled = false;
    
    /**
     * Enable push notifications
     */
    @Builder.Default
    private Boolean pushEnabled = true;
    
    /**
     * Enable in-app notifications
     */
    @Builder.Default
    private Boolean inAppEnabled = true;
    
    /**
     * Minimum severity for email notifications
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AlertSeverity emailMinSeverity = AlertSeverity.WARNING;
    
    /**
     * Minimum severity for SMS notifications
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AlertSeverity smsMinSeverity = AlertSeverity.CRITICAL;
    
    /**
     * Quiet hours start (no notifications)
     */
    private Integer quietHoursStart;
    
    /**
     * Quiet hours end
     */
    private Integer quietHoursEnd;
    
    /**
     * Alert types to receive (comma-separated, null = all)
     */
    private String enabledAlertTypes;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
