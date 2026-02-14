package com.aquasmart.irrigationservice.service.impl;

import com.aquasmart.irrigationservice.dto.*;
import com.aquasmart.irrigationservice.model.*;
import com.aquasmart.irrigationservice.repository.IrrigationEventRepository;
import com.aquasmart.irrigationservice.repository.IrrigationScheduleRepository;
import com.aquasmart.irrigationservice.service.IrrigationEventService;
import com.aquasmart.irrigationservice.service.SmartIrrigationService;
import com.aquasmart.irrigationservice.service.WaterUsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class IrrigationEventServiceImpl implements IrrigationEventService {
    
    private final IrrigationEventRepository eventRepository;
    private final IrrigationScheduleRepository scheduleRepository;
    private final SmartIrrigationService smartIrrigationService;
    private final WaterUsageService waterUsageService;
    
    @Override
    public IrrigationEventResponse createEvent(IrrigationEventRequest request) {
        log.info("Creating irrigation event for parcel: {}", request.getParcelId());
        
        IrrigationSchedule schedule = null;
        if (request.getScheduleId() != null) {
            schedule = scheduleRepository.findById(request.getScheduleId())
                    .orElse(null);
        }
        
        IrrigationEvent event = IrrigationEvent.builder()
                .schedule(schedule)
                .parcelId(request.getParcelId())
                .farmId(request.getFarmId())
                .source(request.getSource())
                .status(IrrigationStatus.SCHEDULED)
                .scheduledStartTime(request.getScheduledStartTime())
                .scheduledEndTime(request.getScheduledEndTime())
                .plannedWaterAmount(request.getPlannedWaterAmount())
                .soilMoistureBefore(request.getSoilMoistureBefore())
                .temperature(request.getTemperature())
                .humidity(request.getHumidity())
                .windSpeed(request.getWindSpeed())
                .notes(request.getNotes())
                .build();
        
        event = eventRepository.save(event);
        log.info("Created irrigation event with ID: {}", event.getId());
        
        return mapToResponse(event);
    }
    
    @Override
    public IrrigationEventResponse startEvent(Long id) {
        log.info("Starting irrigation event: {}", id);
        
        IrrigationEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));
        
        if (event.getStatus() != IrrigationStatus.SCHEDULED) {
            throw new RuntimeException("Event cannot be started. Current status: " + event.getStatus());
        }
        
        event.setStatus(IrrigationStatus.IN_PROGRESS);
        event.setActualStartTime(LocalDateTime.now());
        
        event = eventRepository.save(event);
        log.info("Started irrigation event: {}", id);
        
        return mapToResponse(event);
    }
    
    @Override
    public IrrigationEventResponse completeEvent(Long id, Double actualWaterAmount, Double soilMoistureAfter) {
        log.info("Completing irrigation event: {}", id);
        
        IrrigationEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));
        
        if (event.getStatus() != IrrigationStatus.IN_PROGRESS) {
            throw new RuntimeException("Event cannot be completed. Current status: " + event.getStatus());
        }
        
        event.setStatus(IrrigationStatus.COMPLETED);
        event.setActualEndTime(LocalDateTime.now());
        event.setActualWaterAmount(actualWaterAmount);
        event.setSoilMoistureAfter(soilMoistureAfter);
        
        // Calculate water saved
        if (event.getPlannedWaterAmount() != null && actualWaterAmount != null) {
            double saved = event.getPlannedWaterAmount() - actualWaterAmount;
            event.setWaterSaved(saved > 0 ? saved : 0);
        }
        
        // Calculate efficiency score
        Integer efficiencyScore = smartIrrigationService.evaluateEfficiency(
                event.getPlannedWaterAmount(),
                actualWaterAmount,
                event.getSoilMoistureBefore(),
                soilMoistureAfter,
                event.getTemperature()
        );
        event.setEfficiencyScore(efficiencyScore);
        
        event = eventRepository.save(event);
        
        // Record water usage
        waterUsageService.recordWaterUsage(
                event.getFarmId(),
                event.getParcelId(),
                event.getId(),
                actualWaterAmount,
                event.getWaterSaved()
        );
        
        log.info("Completed irrigation event: {} with efficiency score: {}", id, efficiencyScore);
        
        return mapToResponse(event);
    }
    
    @Override
    public IrrigationEventResponse cancelEvent(Long id, String reason) {
        log.info("Cancelling irrigation event: {}", id);
        
        IrrigationEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));
        
        event.setStatus(IrrigationStatus.CANCELLED);
        event.setSkipReason(reason);
        
        event = eventRepository.save(event);
        log.info("Cancelled irrigation event: {}", id);
        
        return mapToResponse(event);
    }
    
    @Override
    public IrrigationEventResponse skipEvent(Long id, String reason) {
        log.info("Skipping irrigation event: {}", id);
        
        IrrigationEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));
        
        event.setStatus(IrrigationStatus.SKIPPED);
        event.setSkipReason(reason);
        
        // Calculate water saved by skipping (e.g., due to rain)
        event.setWaterSaved(event.getPlannedWaterAmount());
        
        event = eventRepository.save(event);
        
        // Record water saved
        waterUsageService.recordWaterUsage(
                event.getFarmId(),
                event.getParcelId(),
                event.getId(),
                0.0,
                event.getWaterSaved()
        );
        
        log.info("Skipped irrigation event: {} - Reason: {}", id, reason);
        
        return mapToResponse(event);
    }
    
    @Override
    @Transactional(readOnly = true)
    public IrrigationEventResponse getEventById(Long id) {
        IrrigationEvent event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));
        return mapToResponse(event);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<IrrigationEventResponse> getEventsByFarmId(Long farmId, Pageable pageable) {
        return eventRepository.findByFarmId(farmId, pageable)
                .map(this::mapToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<IrrigationEventResponse> getEventsByParcelId(Long parcelId, Pageable pageable) {
        return eventRepository.findByParcelId(parcelId, pageable)
                .map(this::mapToResponse);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<IrrigationEventResponse> getEventsByScheduleId(Long scheduleId) {
        return eventRepository.findByScheduleId(scheduleId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<IrrigationEventResponse> getEventsByStatus(IrrigationStatus status) {
        return eventRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<IrrigationEventResponse> getEventsByFarmIdAndTimeRange(Long farmId, 
                                                                        LocalDateTime start, 
                                                                        LocalDateTime end) {
        return eventRepository.findByFarmIdAndTimeRange(farmId, start, end).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<IrrigationEventResponse> getPendingEvents() {
        return eventRepository.findPendingEvents(LocalDateTime.now()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<IrrigationEventResponse> getInProgressEvents() {
        return eventRepository.findInProgressEvents().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public IrrigationEventResponse triggerManualIrrigation(Long parcelId, Long farmId, 
                                                            Integer durationMinutes, Double waterAmount) {
        log.info("Triggering manual irrigation for parcel: {}", parcelId);
        
        LocalDateTime now = LocalDateTime.now();
        
        IrrigationEvent event = IrrigationEvent.builder()
                .parcelId(parcelId)
                .farmId(farmId)
                .source(IrrigationSource.MANUAL)
                .status(IrrigationStatus.IN_PROGRESS)
                .scheduledStartTime(now)
                .scheduledEndTime(now.plusMinutes(durationMinutes))
                .actualStartTime(now)
                .plannedWaterAmount(waterAmount)
                .build();
        
        event = eventRepository.save(event);
        log.info("Started manual irrigation event: {}", event.getId());
        
        return mapToResponse(event);
    }
    
    private IrrigationEventResponse mapToResponse(IrrigationEvent event) {
        return IrrigationEventResponse.builder()
                .id(event.getId())
                .scheduleId(event.getSchedule() != null ? event.getSchedule().getId() : null)
                .scheduleName(event.getSchedule() != null ? event.getSchedule().getName() : null)
                .parcelId(event.getParcelId())
                .farmId(event.getFarmId())
                .source(event.getSource())
                .status(event.getStatus())
                .scheduledStartTime(event.getScheduledStartTime())
                .scheduledEndTime(event.getScheduledEndTime())
                .actualStartTime(event.getActualStartTime())
                .actualEndTime(event.getActualEndTime())
                .plannedWaterAmount(event.getPlannedWaterAmount())
                .actualWaterAmount(event.getActualWaterAmount())
                .soilMoistureBefore(event.getSoilMoistureBefore())
                .soilMoistureAfter(event.getSoilMoistureAfter())
                .temperature(event.getTemperature())
                .humidity(event.getHumidity())
                .windSpeed(event.getWindSpeed())
                .skipReason(event.getSkipReason())
                .notes(event.getNotes())
                .waterSaved(event.getWaterSaved())
                .efficiencyScore(event.getEfficiencyScore())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }
}
