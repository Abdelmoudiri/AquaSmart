package com.aquasmart.alertservice.dto;

import com.aquasmart.alertservice.model.AlertSeverity;
import com.aquasmart.alertservice.model.AlertType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    private Long farmId;
    
    private Long parcelId;
    
    @NotNull(message = "Alert type is required")
    private AlertType type;
    
    @Builder.Default
    private AlertSeverity severity = AlertSeverity.INFO;
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;
    
    @Size(max = 2000, message = "Message must be less than 2000 characters")
    private String message;
    
    private String source;
    
    private Double triggerValue;
    
    private Double thresholdValue;
    
    private String recommendedAction;
    
    private String metadata;
}
