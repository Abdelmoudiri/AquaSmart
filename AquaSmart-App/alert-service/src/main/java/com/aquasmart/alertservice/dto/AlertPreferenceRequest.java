package com.aquasmart.alertservice.dto;

import com.aquasmart.alertservice.model.AlertSeverity;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertPreferenceRequest {
    
    private Long userId;
    
    private String email;
    
    private String phoneNumber;
    
    @Builder.Default
    private Boolean emailEnabled = true;
    
    @Builder.Default
    private Boolean smsEnabled = false;
    
    @Builder.Default
    private Boolean pushEnabled = true;
    
    @Builder.Default
    private Boolean inAppEnabled = true;
    
    @Builder.Default
    private AlertSeverity emailMinSeverity = AlertSeverity.WARNING;
    
    @Builder.Default
    private AlertSeverity smsMinSeverity = AlertSeverity.CRITICAL;
    
    private Integer quietHoursStart;
    
    private Integer quietHoursEnd;
    
    private String enabledAlertTypes;
}
