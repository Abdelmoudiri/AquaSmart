package com.aquasmart.alertservice.service;

import com.aquasmart.alertservice.dto.*;
import com.aquasmart.alertservice.model.AlertSeverity;
import com.aquasmart.alertservice.model.AlertStatus;
import com.aquasmart.alertservice.model.AlertType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AlertService {
    
    AlertResponse createAlert(AlertRequest request);
    
    AlertResponse getAlertById(Long id);
    
    Page<AlertResponse> getAlertsByUserId(Long userId, Pageable pageable);
    
    Page<AlertResponse> getAlertsByFarmId(Long farmId, Pageable pageable);
    
    Page<AlertResponse> getAlertsByParcelId(Long parcelId, Pageable pageable);
    
    List<AlertResponse> getNewAlertsByUserId(Long userId);
    
    List<AlertResponse> getUrgentAlertsByUserId(Long userId);
    
    List<AlertResponse> getRecentAlerts(Long userId, int limit);
    
    AlertResponse markAsRead(Long id);
    
    AlertResponse acknowledge(Long id);
    
    AlertResponse resolve(Long id, Long resolvedBy, String notes);
    
    AlertResponse dismiss(Long id);
    
    void deleteAlert(Long id);
    
    long getNewAlertsCount(Long userId);
    
    AlertSummaryResponse getAlertSummary(Long userId);
    
    AlertSummaryResponse getFarmAlertSummary(Long farmId);
}
