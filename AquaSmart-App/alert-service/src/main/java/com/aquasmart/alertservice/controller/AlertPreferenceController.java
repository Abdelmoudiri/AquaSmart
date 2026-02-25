package com.aquasmart.alertservice.controller;

import com.aquasmart.alertservice.dto.*;
import com.aquasmart.alertservice.service.AlertPreferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts/preferences")
@RequiredArgsConstructor
@Slf4j
public class AlertPreferenceController {
    
    private final AlertPreferenceService preferenceService;
    
    @GetMapping("/{userId}")
    public ResponseEntity<AlertPreferenceResponse> getPreferences(@PathVariable Long userId) {
        AlertPreferenceResponse response = preferenceService.getPreferences(userId);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{userId}")
    public ResponseEntity<AlertPreferenceResponse> updatePreferences(
            @PathVariable Long userId,
            @RequestBody AlertPreferenceRequest request) {
        log.info("Updating preferences for user: {}", userId);
        AlertPreferenceResponse response = preferenceService.createOrUpdatePreferences(userId, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deletePreferences(@PathVariable Long userId) {
        log.info("Deleting preferences for user: {}", userId);
        preferenceService.deletePreferences(userId);
        return ResponseEntity.noContent().build();
    }
}
