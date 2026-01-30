package com.aquasmart.farmservice.service;

import com.aquasmart.farmservice.dto.CropRequest;
import com.aquasmart.farmservice.dto.CropResponse;
import com.aquasmart.farmservice.model.CropType;
import com.aquasmart.farmservice.model.GrowthStage;

import java.time.LocalDate;
import java.util.List;

public interface CropService {

    CropResponse createCrop(Long parcelId, CropRequest request);

    CropResponse getCropById(Long id);

    List<CropResponse> getCropsByParcel(Long parcelId);

    List<CropResponse> getActiveCropsByParcel(Long parcelId);

    List<CropResponse> getCropsByFarm(Long farmId);

    List<CropResponse> getActiveCropsByFarm(Long farmId);

    List<CropResponse> getCropsByType(CropType cropType);

    List<CropResponse> getCropsByGrowthStage(GrowthStage growthStage);

    List<CropResponse> getCropsReadyForHarvest(LocalDate startDate, LocalDate endDate);

    List<CropResponse> getCropsReadyForHarvestByFarm(Long farmId, LocalDate startDate, LocalDate endDate);

    CropResponse updateCrop(Long id, CropRequest request);

    CropResponse updateGrowthStage(Long id, GrowthStage stage);

    CropResponse harvestCrop(Long id, LocalDate harvestDate);

    CropResponse deactivateCrop(Long id);

    long countCropsByParcel(Long parcelId);

    long countActiveCropsByParcel(Long parcelId);

    void deleteCrop(Long id);
}
