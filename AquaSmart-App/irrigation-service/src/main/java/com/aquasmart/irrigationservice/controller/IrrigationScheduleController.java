package com.aquasmart.irrigationservice.controller;

import com.aquasmart.irrigationservice.dto.*;
import com.aquasmart.irrigationservice.service.IrrigationScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/irrigation/schedules")
@RequiredArgsConstructor
@Slf4j
public class IrrigationScheduleController {
    
    private final IrrigationScheduleService scheduleService;
    
    @PostMapping
    public ResponseEntity<IrrigationScheduleResponse> createSchedule(
            @Valid @RequestBody IrrigationScheduleRequest request) {
        log.info("Creating irrigation schedule for parcel: {}", request.getParcelId());
        IrrigationScheduleResponse response = scheduleService.createSchedule(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<IrrigationScheduleResponse> updateSchedule(
            @PathVariable Long id,
            @Valid @RequestBody IrrigationScheduleRequest request) {
        log.info("Updating irrigation schedule: {}", id);
        IrrigationScheduleResponse response = scheduleService.updateSchedule(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        log.info("Deleting irrigation schedule: {}", id);
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<IrrigationScheduleResponse> getScheduleById(@PathVariable Long id) {
        IrrigationScheduleResponse response = scheduleService.getScheduleById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/farm/{farmId}")
    public ResponseEntity<List<IrrigationScheduleResponse>> getSchedulesByFarmId(
            @PathVariable Long farmId) {
        List<IrrigationScheduleResponse> schedules = scheduleService.getSchedulesByFarmId(farmId);
        return ResponseEntity.ok(schedules);
    }
    
    @GetMapping("/parcel/{parcelId}")
    public ResponseEntity<List<IrrigationScheduleResponse>> getSchedulesByParcelId(
            @PathVariable Long parcelId) {
        List<IrrigationScheduleResponse> schedules = scheduleService.getSchedulesByParcelId(parcelId);
        return ResponseEntity.ok(schedules);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<IrrigationScheduleResponse>> getActiveSchedules() {
        List<IrrigationScheduleResponse> schedules = scheduleService.getActiveSchedules();
        return ResponseEntity.ok(schedules);
    }
    
    @PostMapping("/{id}/activate")
    public ResponseEntity<IrrigationScheduleResponse> activateSchedule(@PathVariable Long id) {
        log.info("Activating irrigation schedule: {}", id);
        IrrigationScheduleResponse response = scheduleService.activateSchedule(id);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<IrrigationScheduleResponse> deactivateSchedule(@PathVariable Long id) {
        log.info("Deactivating irrigation schedule: {}", id);
        IrrigationScheduleResponse response = scheduleService.deactivateSchedule(id);
        return ResponseEntity.ok(response);
    }
}
