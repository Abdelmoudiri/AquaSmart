package com.aquasmart.alertservice.service.impl;

import com.aquasmart.alertservice.dto.AlertPreferenceRequest;
import com.aquasmart.alertservice.dto.AlertPreferenceResponse;
import com.aquasmart.alertservice.model.AlertPreference;
import com.aquasmart.alertservice.model.AlertSeverity;
import com.aquasmart.alertservice.repository.AlertPreferenceRepository;
import com.aquasmart.alertservice.service.AlertPreferenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AlertPreferenceServiceImpl implements AlertPreferenceService {
    
    private final AlertPreferenceRepository preferenceRepository;
    
    @Override
    @Transactional(readOnly = true)
    public AlertPreferenceResponse getPreferences(Long userId) {
        AlertPreference preference = preferenceRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreference(userId));
        return mapToResponse(preference);
    }
    
    @Override
    public AlertPreferenceResponse createOrUpdatePreferences(Long userId, AlertPreferenceRequest request) {
        log.info("Updating alert preferences for user: {}", userId);
        
        AlertPreference preference = preferenceRepository.findByUserId(userId)
                .orElse(AlertPreference.builder().userId(userId).build());
        
        preference.setEmail(request.getEmail());
        preference.setPhoneNumber(request.getPhoneNumber());
        preference.setEmailEnabled(request.getEmailEnabled());
        preference.setSmsEnabled(request.getSmsEnabled());
        preference.setPushEnabled(request.getPushEnabled());
        preference.setInAppEnabled(request.getInAppEnabled());
        preference.setEmailMinSeverity(request.getEmailMinSeverity());
        preference.setSmsMinSeverity(request.getSmsMinSeverity());
        preference.setQuietHoursStart(request.getQuietHoursStart());
        preference.setQuietHoursEnd(request.getQuietHoursEnd());
        preference.setEnabledAlertTypes(request.getEnabledAlertTypes());
        
        preference = preferenceRepository.save(preference);
        log.info("Updated alert preferences for user: {}", userId);
        
        return mapToResponse(preference);
    }
    
    @Override
    public void deletePreferences(Long userId) {
        log.info("Deleting alert preferences for user: {}", userId);
        preferenceRepository.findByUserId(userId)
                .ifPresent(preferenceRepository::delete);
    }
    
    private AlertPreference createDefaultPreference(Long userId) {
        AlertPreference preference = AlertPreference.builder()
                .userId(userId)
                .emailEnabled(true)
                .smsEnabled(false)
                .pushEnabled(true)
                .inAppEnabled(true)
                .emailMinSeverity(AlertSeverity.WARNING)
                .smsMinSeverity(AlertSeverity.CRITICAL)
                .build();
        return preferenceRepository.save(preference);
    }
    
    private AlertPreferenceResponse mapToResponse(AlertPreference preference) {
        return AlertPreferenceResponse.builder()
                .id(preference.getId())
                .userId(preference.getUserId())
                .email(preference.getEmail())
                .phoneNumber(preference.getPhoneNumber())
                .emailEnabled(preference.getEmailEnabled())
                .smsEnabled(preference.getSmsEnabled())
                .pushEnabled(preference.getPushEnabled())
                .inAppEnabled(preference.getInAppEnabled())
                .emailMinSeverity(preference.getEmailMinSeverity())
                .smsMinSeverity(preference.getSmsMinSeverity())
                .quietHoursStart(preference.getQuietHoursStart())
                .quietHoursEnd(preference.getQuietHoursEnd())
                .enabledAlertTypes(preference.getEnabledAlertTypes())
                .createdAt(preference.getCreatedAt())
                .updatedAt(preference.getUpdatedAt())
                .build();
    }
}
