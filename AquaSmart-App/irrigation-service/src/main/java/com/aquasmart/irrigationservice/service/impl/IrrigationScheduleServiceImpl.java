package com.aquasmart.irrigationservice.service.impl;

import com.aquasmart.irrigationservice.dto.*;
import com.aquasmart.irrigationservice.model.*;
import com.aquasmart.irrigationservice.repository.IrrigationEventRepository;
import com.aquasmart.irrigationservice.repository.IrrigationScheduleRepository;
import com.aquasmart.irrigationservice.service.IrrigationScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class IrrigationScheduleServiceImpl implements IrrigationScheduleService {
    
    private final IrrigationScheduleRepository scheduleRepository;
    private final IrrigationEventRepository eventRepository;
    
    @Override
    public IrrigationScheduleResponse createSchedule(IrrigationScheduleRequest request) {
        log.info("Creating irrigation schedule for parcel: {}", request.getParcelId());
        
        IrrigationSchedule schedule = IrrigationSchedule.builder()
                .parcelId(request.getParcelId())
                .farmId(request.getFarmId())
                .name(request.getName())
                .description(request.getDescription())
                .irrigationType(request.getIrrigationType())
                .startTime(request.getStartTime())
                .durationMinutes(request.getDurationMinutes())
                .waterAmountPerSquareMeter(request.getWaterAmountPerSquareMeter())
                .activeDays(request.getActiveDays())
                .active(request.getActive() != null ? request.getActive() : true)
                .weatherAware(request.getWeatherAware() != null ? request.getWeatherAware() : true)
                .sensorAware(request.getSensorAware() != null ? request.getSensorAware() : true)
                .minSoilMoistureThreshold(request.getMinSoilMoistureThreshold())
                .maxSoilMoistureThreshold(request.getMaxSoilMoistureThreshold())
                .priority(request.getPriority() != null ? request.getPriority() : IrrigationPriority.NORMAL)
                .build();
        
        schedule = scheduleRepository.save(schedule);
        log.info("Created irrigation schedule with ID: {}", schedule.getId());
        
        return mapToResponse(schedule);
    }
    
    @Override
    public IrrigationScheduleResponse updateSchedule(Long id, IrrigationScheduleRequest request) {
        log.info("Updating irrigation schedule: {}", id);
        
        IrrigationSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found: " + id));
        
        schedule.setName(request.getName());
        schedule.setDescription(request.getDescription());
        schedule.setIrrigationType(request.getIrrigationType());
        schedule.setStartTime(request.getStartTime());
        schedule.setDurationMinutes(request.getDurationMinutes());
        schedule.setWaterAmountPerSquareMeter(request.getWaterAmountPerSquareMeter());
        schedule.setActiveDays(request.getActiveDays());
        schedule.setWeatherAware(request.getWeatherAware());
        schedule.setSensorAware(request.getSensorAware());
        schedule.setMinSoilMoistureThreshold(request.getMinSoilMoistureThreshold());
        schedule.setMaxSoilMoistureThreshold(request.getMaxSoilMoistureThreshold());
        schedule.setPriority(request.getPriority());
        
        schedule = scheduleRepository.save(schedule);
        return mapToResponse(schedule);
    }
    
    @Override
    public void deleteSchedule(Long id) {
        log.info("Deleting irrigation schedule: {}", id);
        
        if (!scheduleRepository.existsById(id)) {
            throw new RuntimeException("Schedule not found: " + id);
        }
        
        scheduleRepository.deleteById(id);
        log.info("Deleted irrigation schedule: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public IrrigationScheduleResponse getScheduleById(Long id) {
        IrrigationSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found: " + id));
        return mapToResponseWithStats(schedule);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<IrrigationScheduleResponse> getSchedulesByFarmId(Long farmId) {
        return scheduleRepository.findByFarmId(farmId).stream()
                .map(this::mapToResponseWithStats)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<IrrigationScheduleResponse> getSchedulesByParcelId(Long parcelId) {
        return scheduleRepository.findByParcelId(parcelId).stream()
                .map(this::mapToResponseWithStats)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<IrrigationScheduleResponse> getActiveSchedules() {
        return scheduleRepository.findByActiveTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public IrrigationScheduleResponse activateSchedule(Long id) {
        log.info("Activating irrigation schedule: {}", id);
        
        IrrigationSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found: " + id));
        
        schedule.setActive(true);
        schedule = scheduleRepository.save(schedule);
        
        return mapToResponse(schedule);
    }
    
    @Override
    public IrrigationScheduleResponse deactivateSchedule(Long id) {
        log.info("Deactivating irrigation schedule: {}", id);
        
        IrrigationSchedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found: " + id));
        
        schedule.setActive(false);
        schedule = scheduleRepository.save(schedule);
        
        return mapToResponse(schedule);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<IrrigationScheduleResponse> getSchedulesForExecution(LocalDateTime start, LocalDateTime end) {
        LocalTime startTime = start.toLocalTime();
        LocalTime endTime = end.toLocalTime();
        
        return scheduleRepository.findActiveSchedulesInTimeRange(startTime, endTime).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private IrrigationScheduleResponse mapToResponse(IrrigationSchedule schedule) {
        return IrrigationScheduleResponse.builder()
                .id(schedule.getId())
                .parcelId(schedule.getParcelId())
                .farmId(schedule.getFarmId())
                .name(schedule.getName())
                .description(schedule.getDescription())
                .irrigationType(schedule.getIrrigationType())
                .startTime(schedule.getStartTime())
                .durationMinutes(schedule.getDurationMinutes())
                .waterAmountPerSquareMeter(schedule.getWaterAmountPerSquareMeter())
                .activeDays(schedule.getActiveDays())
                .active(schedule.getActive())
                .weatherAware(schedule.getWeatherAware())
                .sensorAware(schedule.getSensorAware())
                .minSoilMoistureThreshold(schedule.getMinSoilMoistureThreshold())
                .maxSoilMoistureThreshold(schedule.getMaxSoilMoistureThreshold())
                .priority(schedule.getPriority())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }
    
    private IrrigationScheduleResponse mapToResponseWithStats(IrrigationSchedule schedule) {
        IrrigationScheduleResponse response = mapToResponse(schedule);
        
        // Get recent events
        List<IrrigationEvent> recentEvents = eventRepository.findTop10ByScheduleIdOrderByCreatedAtDesc(schedule.getId());
        
        response.setTotalEventsCount(schedule.getEvents().size());
        response.setCompletedEventsCount((int) schedule.getEvents().stream()
                .filter(e -> e.getStatus() == IrrigationStatus.COMPLETED)
                .count());
        response.setTotalWaterUsed(schedule.getEvents().stream()
                .filter(e -> e.getActualWaterAmount() != null)
                .mapToDouble(IrrigationEvent::getActualWaterAmount)
                .sum());
        response.setTotalWaterSaved(schedule.getEvents().stream()
                .filter(e -> e.getWaterSaved() != null)
                .mapToDouble(IrrigationEvent::getWaterSaved)
                .sum());
        
        response.setRecentEvents(recentEvents.stream()
                .map(this::mapEventToResponse)
                .collect(Collectors.toList()));
        
        return response;
    }
    
    private IrrigationEventResponse mapEventToResponse(IrrigationEvent event) {
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
