package com.aquasmart.alertservice.service;

import com.aquasmart.alertservice.dto.AlertPreferenceRequest;
import com.aquasmart.alertservice.dto.AlertPreferenceResponse;

public interface AlertPreferenceService {
    
    AlertPreferenceResponse getPreferences(Long userId);
    
    AlertPreferenceResponse createOrUpdatePreferences(Long userId, AlertPreferenceRequest request);
    
    void deletePreferences(Long userId);
}
