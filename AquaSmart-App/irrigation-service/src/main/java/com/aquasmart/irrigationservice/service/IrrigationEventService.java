package com.aquasmart.irrigationservice.service;

import com.aquasmart.irrigationservice.dto.*;
import com.aquasmart.irrigationservice.model.IrrigationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface IrrigationEventService {
    
    IrrigationEventResponse createEvent(IrrigationEventRequest request);
    
    IrrigationEventResponse startEvent(Long id);
    
    IrrigationEventResponse completeEvent(Long id, Double actualWaterAmount, Double soilMoistureAfter);
    
    IrrigationEventResponse cancelEvent(Long id, String reason);
    
    IrrigationEventResponse skipEvent(Long id, String reason);
    
    IrrigationEventResponse getEventById(Long id);
    
    Page<IrrigationEventResponse> getEventsByFarmId(Long farmId, Pageable pageable);
    
    Page<IrrigationEventResponse> getEventsByParcelId(Long parcelId, Pageable pageable);
    
    List<IrrigationEventResponse> getEventsByScheduleId(Long scheduleId);
    
    List<IrrigationEventResponse> getEventsByStatus(IrrigationStatus status);
    
    List<IrrigationEventResponse> getEventsByFarmIdAndTimeRange(Long farmId, LocalDateTime start, LocalDateTime end);
    
    List<IrrigationEventResponse> getPendingEvents();
    
    List<IrrigationEventResponse> getInProgressEvents();
    
    /**
     * Trigger manual irrigation for a parcel
     */
    IrrigationEventResponse triggerManualIrrigation(Long parcelId, Long farmId, 
                                                     Integer durationMinutes, Double waterAmount);
}
