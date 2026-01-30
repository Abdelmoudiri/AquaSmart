package com.aquasmart.farmservice.service;

import com.aquasmart.farmservice.dto.ParcelRequest;
import com.aquasmart.farmservice.dto.ParcelResponse;
import com.aquasmart.farmservice.model.ParcelStatus;
import com.aquasmart.farmservice.model.SoilType;

import java.util.List;

public interface ParcelService {

    ParcelResponse createParcel(Long farmId, ParcelRequest request);

    ParcelResponse getParcelById(Long id);

    ParcelResponse getParcelWithCrops(Long id);

    List<ParcelResponse> getParcelsByFarm(Long farmId);

    List<ParcelResponse> getParcelsByFarmAndStatus(Long farmId, ParcelStatus status);

    List<ParcelResponse> getParcelsByFarmAndSoilType(Long farmId, SoilType soilType);

    List<ParcelResponse> getParcelsNeedingIrrigation();

    List<ParcelResponse> getParcelsNeedingIrrigationByFarm(Long farmId);

    ParcelResponse updateParcel(Long id, ParcelRequest request);

    ParcelResponse updateParcelStatus(Long id, ParcelStatus status);

    ParcelResponse updateParcelMoisture(Long id, Double moisture);

    ParcelResponse recordIrrigation(Long id, Double amount);

    long countParcelsByFarm(Long farmId);

    Double getTotalAreaByFarm(Long farmId);

    void deleteParcel(Long id);
}
