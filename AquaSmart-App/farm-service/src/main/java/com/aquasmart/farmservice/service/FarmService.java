package com.aquasmart.farmservice.service;

import com.aquasmart.farmservice.dto.FarmRequest;
import com.aquasmart.farmservice.dto.FarmResponse;
import com.aquasmart.farmservice.model.FarmStatus;

import java.util.List;

public interface FarmService {

    FarmResponse createFarm(FarmRequest request, Long ownerId);

    FarmResponse getFarmById(Long id);

    FarmResponse getFarmByIdAndOwner(Long id, Long ownerId);

    List<FarmResponse> getAllFarms();

    List<FarmResponse> getFarmsByOwner(Long ownerId);

    List<FarmResponse> getFarmsByStatus(FarmStatus status);

    List<FarmResponse> searchFarms(String keyword);

    FarmResponse updateFarm(Long id, FarmRequest request, Long ownerId);

    FarmResponse updateFarmStatus(Long id, FarmStatus status, Long ownerId);

    void deleteFarm(Long id, Long ownerId);

    FarmResponse getFarmWithParcels(Long id);
}
