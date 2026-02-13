package com.aquasmart.irrigationservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Fallback for Farm Client when service is unavailable
 */
@Component
@Slf4j
public class FarmClientFallback implements FarmClient {
    
    @Override
    public Map<String, Object> getFarmById(Long id) {
        log.warn("Farm service unavailable, returning fallback for getFarmById: {}", id);
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("error", true);
        fallback.put("message", "Farm service temporarily unavailable");
        fallback.put("id", id);
        return fallback;
    }
    
    @Override
    public Map<String, Object> getParcelById(Long id) {
        log.warn("Farm service unavailable, returning fallback for getParcelById: {}", id);
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("error", true);
        fallback.put("message", "Farm service temporarily unavailable");
        fallback.put("id", id);
        return fallback;
    }
    
    @Override
    public Map<String, Object> getParcelsByFarmId(Long farmId) {
        log.warn("Farm service unavailable, returning fallback for getParcelsByFarmId: {}", farmId);
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("error", true);
        fallback.put("message", "Farm service temporarily unavailable");
        fallback.put("farmId", farmId);
        return fallback;
    }
}
