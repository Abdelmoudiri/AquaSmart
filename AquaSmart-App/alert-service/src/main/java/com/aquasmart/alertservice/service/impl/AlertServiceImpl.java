package com.aquasmart.alertservice.service.impl;

import com.aquasmart.alertservice.dto.*;
import com.aquasmart.alertservice.model.*;
import com.aquasmart.alertservice.repository.AlertRepository;
import com.aquasmart.alertservice.service.AlertService;
import com.aquasmart.alertservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AlertServiceImpl implements AlertService {
    
    private final AlertRepository alertRepository;
    private final NotificationService notificationService;
    
    @Override
    public AlertResponse createAlert(AlertRequest request) {
        log.info("Creating alert for user: {} - Type: {}", request.getUserId(), request.getType());
        
        Alert alert = Alert.builder()
                .userId(request.getUserId())
                .farmId(request.getFarmId())
                .parcelId(request.getParcelId())
                .type(request.getType())
                .severity(request.getSeverity() != null ? request.getSeverity() : AlertSeverity.INFO)
                .status(AlertStatus.NEW)
                .title(request.getTitle())
                .message(request.getMessage())
                .source(request.getSource())
                .triggerValue(request.getTriggerValue())
                .thresholdValue(request.getThresholdValue())
                .recommendedAction(request.getRecommendedAction())
                .metadata(request.getMetadata())
                .build();
        
        alert = alertRepository.save(alert);
        log.info("Created alert with ID: {}", alert.getId());
        
        // Send notification asynchronously
        notificationService.sendNotification(alert);
        
        return mapToResponse(alert);
    }
    
    @Override
    @Transactional(readOnly = true)
    public AlertResponse getAlertById(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + id));
        return mapToResponse(alert);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<AlertResponse> getAlertsByUserId(Long userId, Pageable pageable) {
        return alertRepository.findByUserId(userId, pageable)
                .map(this::mapToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<AlertResponse> getAlertsByFarmId(Long farmId, Pageable pageable) {
        return alertRepository.findByFarmId(farmId, pageable)
                .map(this::mapToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<AlertResponse> getAlertsByParcelId(Long parcelId, Pageable pageable) {
        return alertRepository.findByParcelId(parcelId, pageable)
                .map(this::mapToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AlertResponse> getNewAlertsByUserId(Long userId) {
        return alertRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, AlertStatus.NEW)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AlertResponse> getUrgentAlertsByUserId(Long userId) {
        return alertRepository.findUrgentAlertsByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<AlertResponse> getRecentAlerts(Long userId, int limit) {
        return alertRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public AlertResponse markAsRead(Long id) {
        log.info("Marking alert as read: {}", id);
        
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + id));
        
        if (alert.getStatus() == AlertStatus.NEW) {
            alert.setStatus(AlertStatus.READ);
            alert.setReadAt(LocalDateTime.now());
            alert = alertRepository.save(alert);
        }
        
        return mapToResponse(alert);
    }
    
    @Override
    public AlertResponse acknowledge(Long id) {
        log.info("Acknowledging alert: {}", id);
        
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + id));
        
        alert.setStatus(AlertStatus.ACKNOWLEDGED);
        alert.setAcknowledgedAt(LocalDateTime.now());
        if (alert.getReadAt() == null) {
            alert.setReadAt(LocalDateTime.now());
        }
        
        alert = alertRepository.save(alert);
        return mapToResponse(alert);
    }
    
    @Override
    public AlertResponse resolve(Long id, Long resolvedBy, String notes) {
        log.info("Resolving alert: {}", id);
        
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + id));
        
        alert.setStatus(AlertStatus.RESOLVED);
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolvedBy(resolvedBy);
        alert.setResolutionNotes(notes);
        
        alert = alertRepository.save(alert);
        return mapToResponse(alert);
    }
    
    @Override
    public AlertResponse dismiss(Long id) {
        log.info("Dismissing alert: {}", id);
        
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + id));
        
        alert.setStatus(AlertStatus.DISMISSED);
        alert = alertRepository.save(alert);
        
        return mapToResponse(alert);
    }
    
    @Override
    public void deleteAlert(Long id) {
        log.info("Deleting alert: {}", id);
        
        if (!alertRepository.existsById(id)) {
            throw new RuntimeException("Alert not found: " + id);
        }
        
        alertRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getNewAlertsCount(Long userId) {
        return alertRepository.countNewAlertsByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public AlertSummaryResponse getAlertSummary(Long userId) {
        long newCount = alertRepository.countByUserIdAndStatus(userId, AlertStatus.NEW);
        long readCount = alertRepository.countByUserIdAndStatus(userId, AlertStatus.READ);
        long acknowledgedCount = alertRepository.countByUserIdAndStatus(userId, AlertStatus.ACKNOWLEDGED);
        long resolvedCount = alertRepository.countByUserIdAndStatus(userId, AlertStatus.RESOLVED);
        
        long infoCount = alertRepository.countByUserIdAndSeverity(userId, AlertSeverity.INFO);
        long warningCount = alertRepository.countByUserIdAndSeverity(userId, AlertSeverity.WARNING);
        long criticalCount = alertRepository.countByUserIdAndSeverity(userId, AlertSeverity.CRITICAL);
        long emergencyCount = alertRepository.countByUserIdAndSeverity(userId, AlertSeverity.EMERGENCY);
        
        Map<String, Integer> alertsByType = new HashMap<>();
        List<Object[]> typeStats = alertRepository.countByUserIdGroupByType(userId);
        for (Object[] stat : typeStats) {
            alertsByType.put(stat[0].toString(), ((Long) stat[1]).intValue());
        }
        
        return AlertSummaryResponse.builder()
                .userId(userId)
                .totalAlerts((int) (newCount + readCount + acknowledgedCount + resolvedCount))
                .newAlerts((int) newCount)
                .readAlerts((int) readCount)
                .acknowledgedAlerts((int) acknowledgedCount)
                .resolvedAlerts((int) resolvedCount)
                .infoCount((int) infoCount)
                .warningCount((int) warningCount)
                .criticalCount((int) criticalCount)
                .emergencyCount((int) emergencyCount)
                .alertsByType(alertsByType)
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public AlertSummaryResponse getFarmAlertSummary(Long farmId) {
        List<Alert> alerts = alertRepository.findNewAlertsByFarmId(farmId);
        
        Map<String, Integer> alertsByType = alerts.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getType().name(),
                        Collectors.collectingAndThen(Collectors.counting(), Long::intValue)
                ));
        
        return AlertSummaryResponse.builder()
                .farmId(farmId)
                .totalAlerts(alerts.size())
                .newAlerts(alerts.size())
                .warningCount((int) alerts.stream()
                        .filter(a -> a.getSeverity() == AlertSeverity.WARNING).count())
                .criticalCount((int) alerts.stream()
                        .filter(a -> a.getSeverity() == AlertSeverity.CRITICAL).count())
                .emergencyCount((int) alerts.stream()
                        .filter(a -> a.getSeverity() == AlertSeverity.EMERGENCY).count())
                .alertsByType(alertsByType)
                .build();
    }
    
    private AlertResponse mapToResponse(Alert alert) {
        return AlertResponse.builder()
                .id(alert.getId())
                .userId(alert.getUserId())
                .farmId(alert.getFarmId())
                .parcelId(alert.getParcelId())
                .type(alert.getType())
                .severity(alert.getSeverity())
                .status(alert.getStatus())
                .title(alert.getTitle())
                .message(alert.getMessage())
                .source(alert.getSource())
                .triggerValue(alert.getTriggerValue())
                .thresholdValue(alert.getThresholdValue())
                .recommendedAction(alert.getRecommendedAction())
                .metadata(alert.getMetadata())
                .createdAt(alert.getCreatedAt())
                .readAt(alert.getReadAt())
                .acknowledgedAt(alert.getAcknowledgedAt())
                .resolvedAt(alert.getResolvedAt())
                .resolvedBy(alert.getResolvedBy())
                .resolutionNotes(alert.getResolutionNotes())
                .build();
    }
}
