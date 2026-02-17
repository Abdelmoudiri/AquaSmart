package com.aquasmart.irrigationservice.controller;

import com.aquasmart.irrigationservice.dto.*;
import com.aquasmart.irrigationservice.model.IrrigationStatus;
import com.aquasmart.irrigationservice.service.IrrigationEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/irrigation/events")
@RequiredArgsConstructor
@Slf4j
public class IrrigationEventController {
    
    private final IrrigationEventService eventService;
    
    @PostMapping
    public ResponseEntity<IrrigationEventResponse> createEvent(
            @Valid @RequestBody IrrigationEventRequest request) {
        log.info("Creating irrigation event for parcel: {}", request.getParcelId());
        IrrigationEventResponse response = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<IrrigationEventResponse> getEventById(@PathVariable Long id) {
        IrrigationEventResponse response = eventService.getEventById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/farm/{farmId}")
    public ResponseEntity<Page<IrrigationEventResponse>> getEventsByFarmId(
            @PathVariable Long farmId,
            Pageable pageable) {
        Page<IrrigationEventResponse> events = eventService.getEventsByFarmId(farmId, pageable);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/parcel/{parcelId}")
    public ResponseEntity<Page<IrrigationEventResponse>> getEventsByParcelId(
            @PathVariable Long parcelId,
            Pageable pageable) {
        Page<IrrigationEventResponse> events = eventService.getEventsByParcelId(parcelId, pageable);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<List<IrrigationEventResponse>> getEventsByScheduleId(
            @PathVariable Long scheduleId) {
        List<IrrigationEventResponse> events = eventService.getEventsByScheduleId(scheduleId);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<IrrigationEventResponse>> getEventsByStatus(
            @PathVariable IrrigationStatus status) {
        List<IrrigationEventResponse> events = eventService.getEventsByStatus(status);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/farm/{farmId}/range")
    public ResponseEntity<List<IrrigationEventResponse>> getEventsByFarmIdAndTimeRange(
            @PathVariable Long farmId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<IrrigationEventResponse> events = eventService.getEventsByFarmIdAndTimeRange(farmId, start, end);
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<IrrigationEventResponse>> getPendingEvents() {
        List<IrrigationEventResponse> events = eventService.getPendingEvents();
        return ResponseEntity.ok(events);
    }
    
    @GetMapping("/in-progress")
    public ResponseEntity<List<IrrigationEventResponse>> getInProgressEvents() {
        List<IrrigationEventResponse> events = eventService.getInProgressEvents();
        return ResponseEntity.ok(events);
    }
    
    @PostMapping("/{id}/start")
    public ResponseEntity<IrrigationEventResponse> startEvent(@PathVariable Long id) {
        log.info("Starting irrigation event: {}", id);
        IrrigationEventResponse response = eventService.startEvent(id);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/complete")
    public ResponseEntity<IrrigationEventResponse> completeEvent(
            @PathVariable Long id,
            @RequestParam Double actualWaterAmount,
            @RequestParam(required = false) Double soilMoistureAfter) {
        log.info("Completing irrigation event: {}", id);
        IrrigationEventResponse response = eventService.completeEvent(id, actualWaterAmount, soilMoistureAfter);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<IrrigationEventResponse> cancelEvent(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        log.info("Cancelling irrigation event: {}", id);
        IrrigationEventResponse response = eventService.cancelEvent(id, reason);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/skip")
    public ResponseEntity<IrrigationEventResponse> skipEvent(
            @PathVariable Long id,
            @RequestParam String reason) {
        log.info("Skipping irrigation event: {}", id);
        IrrigationEventResponse response = eventService.skipEvent(id, reason);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/manual")
    public ResponseEntity<IrrigationEventResponse> triggerManualIrrigation(
            @RequestParam Long parcelId,
            @RequestParam Long farmId,
            @RequestParam Integer durationMinutes,
            @RequestParam Double waterAmount) {
        log.info("Triggering manual irrigation for parcel: {}", parcelId);
        IrrigationEventResponse response = eventService.triggerManualIrrigation(
                parcelId, farmId, durationMinutes, waterAmount);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
