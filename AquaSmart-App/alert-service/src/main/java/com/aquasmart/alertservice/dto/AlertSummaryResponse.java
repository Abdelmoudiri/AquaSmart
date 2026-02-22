package com.aquasmart.alertservice.dto;

import lombok.*;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertSummaryResponse {
    
    private Long userId;
    private Long farmId;
    
    private Integer totalAlerts;
    private Integer newAlerts;
    private Integer readAlerts;
    private Integer acknowledgedAlerts;
    private Integer resolvedAlerts;
    
    private Integer infoCount;
    private Integer warningCount;
    private Integer criticalCount;
    private Integer emergencyCount;
    
    private Map<String, Integer> alertsByType;
}
