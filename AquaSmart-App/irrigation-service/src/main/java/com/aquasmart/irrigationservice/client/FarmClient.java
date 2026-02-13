package com.aquasmart.irrigationservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Feign client for Farm Service
 */
@FeignClient(name = "farm-service", fallback = FarmClientFallback.class)
public interface FarmClient {
    
    @GetMapping("/api/farms/{id}")
    Map<String, Object> getFarmById(@PathVariable("id") Long id);
    
    @GetMapping("/api/parcels/{id}")
    Map<String, Object> getParcelById(@PathVariable("id") Long id);
    
    @GetMapping("/api/farms/{farmId}/parcels")
    Map<String, Object> getParcelsByFarmId(@PathVariable("farmId") Long farmId);
}
