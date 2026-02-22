package com.aquasmart.alertservice.dto;

import com.aquasmart.alertservice.model.AlertSeverity;
import com.aquasmart.alertservice.model.AlertStatus;
import com.aquasmart.alertservice.model.AlertType;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertResponse {
    
    private Long id;
    private Long userId;
    private Long farmId;
    private Long parcelId;
    private AlertType type;
    private AlertSeverity severity;
    private AlertStatus status;
    private String title;
    private String message;
    private String source;
    private Double triggerValue;
    private Double thresholdValue;
    private String recommendedAction;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private LocalDateTime acknowledgedAt;
    private LocalDateTime resolvedAt;
    private Long resolvedBy;
    private String resolutionNotes;
}
