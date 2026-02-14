package com.aquasmart.irrigationservice.service;

import com.aquasmart.irrigationservice.dto.*;
import com.aquasmart.irrigationservice.model.IrrigationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface IrrigationScheduleService {
    
    IrrigationScheduleResponse createSchedule(IrrigationScheduleRequest request);
    
    IrrigationScheduleResponse updateSchedule(Long id, IrrigationScheduleRequest request);
    
    void deleteSchedule(Long id);
    
    IrrigationScheduleResponse getScheduleById(Long id);
    
    List<IrrigationScheduleResponse> getSchedulesByFarmId(Long farmId);
    
    List<IrrigationScheduleResponse> getSchedulesByParcelId(Long parcelId);
    
    List<IrrigationScheduleResponse> getActiveSchedules();
    
    IrrigationScheduleResponse activateSchedule(Long id);
    
    IrrigationScheduleResponse deactivateSchedule(Long id);
    
    /**
     * Get schedules that need to be executed within the time range
     */
    List<IrrigationScheduleResponse> getSchedulesForExecution(LocalDateTime start, LocalDateTime end);
}
