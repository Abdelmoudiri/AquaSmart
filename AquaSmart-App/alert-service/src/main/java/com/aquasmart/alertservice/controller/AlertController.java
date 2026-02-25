package com.aquasmart.alertservice.controller;

import com.aquasmart.alertservice.dto.*;
import com.aquasmart.alertservice.service.AlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Slf4j
public class AlertController {
    
    private final AlertService alertService;
    
    @PostMapping
    public ResponseEntity<AlertResponse> createAlert(@Valid @RequestBody AlertRequest request) {
        log.info("Creating alert for user: {}", request.getUserId());
        AlertResponse response = alertService.createAlert(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AlertResponse> getAlertById(@PathVariable Long id) {
        AlertResponse response = alertService.getAlertById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<AlertResponse>> getAlertsByUserId(
            @PathVariable Long userId,
            Pageable pageable) {
        Page<AlertResponse> alerts = alertService.getAlertsByUserId(userId, pageable);
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/farm/{farmId}")
    public ResponseEntity<Page<AlertResponse>> getAlertsByFarmId(
            @PathVariable Long farmId,
            Pageable pageable) {
        Page<AlertResponse> alerts = alertService.getAlertsByFarmId(farmId, pageable);
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/parcel/{parcelId}")
    public ResponseEntity<Page<AlertResponse>> getAlertsByParcelId(
            @PathVariable Long parcelId,
            Pageable pageable) {
        Page<AlertResponse> alerts = alertService.getAlertsByParcelId(parcelId, pageable);
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/user/{userId}/new")
    public ResponseEntity<List<AlertResponse>> getNewAlertsByUserId(@PathVariable Long userId) {
        List<AlertResponse> alerts = alertService.getNewAlertsByUserId(userId);
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/user/{userId}/urgent")
    public ResponseEntity<List<AlertResponse>> getUrgentAlertsByUserId(@PathVariable Long userId) {
        List<AlertResponse> alerts = alertService.getUrgentAlertsByUserId(userId);
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<AlertResponse>> getRecentAlerts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        List<AlertResponse> alerts = alertService.getRecentAlerts(userId, limit);
        return ResponseEntity.ok(alerts);
    }
    
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> getNewAlertsCount(@PathVariable Long userId) {
        long count = alertService.getNewAlertsCount(userId);
        return ResponseEntity.ok(count);
    }
    
    @GetMapping("/user/{userId}/summary")
    public ResponseEntity<AlertSummaryResponse> getAlertSummary(@PathVariable Long userId) {
        AlertSummaryResponse summary = alertService.getAlertSummary(userId);
        return ResponseEntity.ok(summary);
    }
    
    @GetMapping("/farm/{farmId}/summary")
    public ResponseEntity<AlertSummaryResponse> getFarmAlertSummary(@PathVariable Long farmId) {
        AlertSummaryResponse summary = alertService.getFarmAlertSummary(farmId);
        return ResponseEntity.ok(summary);
    }
    
    @PostMapping("/{id}/read")
    public ResponseEntity<AlertResponse> markAsRead(@PathVariable Long id) {
        log.info("Marking alert as read: {}", id);
        AlertResponse response = alertService.markAsRead(id);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/acknowledge")
    public ResponseEntity<AlertResponse> acknowledge(@PathVariable Long id) {
        log.info("Acknowledging alert: {}", id);
        AlertResponse response = alertService.acknowledge(id);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/resolve")
    public ResponseEntity<AlertResponse> resolve(
            @PathVariable Long id,
            @RequestParam Long resolvedBy,
            @RequestParam(required = false) String notes) {
        log.info("Resolving alert: {}", id);
        AlertResponse response = alertService.resolve(id, resolvedBy, notes);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/dismiss")
    public ResponseEntity<AlertResponse> dismiss(@PathVariable Long id) {
        log.info("Dismissing alert: {}", id);
        AlertResponse response = alertService.dismiss(id);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(@PathVariable Long id) {
        log.info("Deleting alert: {}", id);
        alertService.deleteAlert(id);
        return ResponseEntity.noContent().build();
    }
}
