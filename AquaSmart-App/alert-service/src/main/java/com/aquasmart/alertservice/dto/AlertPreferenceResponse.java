package com.aquasmart.alertservice.dto;

import com.aquasmart.alertservice.model.AlertSeverity;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertPreferenceResponse {
    
    private Long id;
    private Long userId;
    private String email;
    private String phoneNumber;
    private Boolean emailEnabled;
    private Boolean smsEnabled;
    private Boolean pushEnabled;
    private Boolean inAppEnabled;
    private AlertSeverity emailMinSeverity;
    private AlertSeverity smsMinSeverity;
    private Integer quietHoursStart;
    private Integer quietHoursEnd;
    private String enabledAlertTypes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
