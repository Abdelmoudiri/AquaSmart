package com.aquasmart.farmservice.service.impl;

import com.aquasmart.farmservice.dto.CropRequest;
import com.aquasmart.farmservice.dto.CropResponse;
import com.aquasmart.farmservice.exception.BadRequestException;
import com.aquasmart.farmservice.exception.ResourceNotFoundException;
import com.aquasmart.farmservice.mapper.CropMapper;
import com.aquasmart.farmservice.model.Crop;
import com.aquasmart.farmservice.model.CropType;
import com.aquasmart.farmservice.model.GrowthStage;
import com.aquasmart.farmservice.model.Parcel;
import com.aquasmart.farmservice.repository.CropRepository;
import com.aquasmart.farmservice.repository.FarmRepository;
import com.aquasmart.farmservice.repository.ParcelRepository;
import com.aquasmart.farmservice.service.CropService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CropServiceImpl implements CropService {

    private final CropRepository cropRepository;
    private final ParcelRepository parcelRepository;
    private final FarmRepository farmRepository;
    private final CropMapper cropMapper;

    @Override
    public CropResponse createCrop(Long parcelId, CropRequest request) {
        log.info("Création d'une nouvelle culture pour la parcelle: {}", parcelId);

        Parcel parcel = parcelRepository.findById(parcelId)
                .orElseThrow(() -> new ResourceNotFoundException("Parcelle", "id", parcelId));

        if (cropRepository.existsByNameAndParcelId(request.getName(), parcelId)) {
            throw new BadRequestException("Une culture avec ce nom existe déjà dans cette parcelle");
        }

        Crop crop = cropMapper.toEntity(request);
        crop.setParcel(parcel);
        crop.setIsActive(true);
        
        if (crop.getGrowthStage() == null) {
            crop.setGrowthStage(GrowthStage.SEEDING);
        }

        Crop savedCrop = cropRepository.save(crop);
        log.info("Culture créée avec succès: {}", savedCrop.getId());

        return cropMapper.toResponse(savedCrop);
    }

    @Override
    @Transactional(readOnly = true)
    public CropResponse getCropById(Long id) {
        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Culture", "id", id));
        return cropMapper.toResponse(crop);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropResponse> getCropsByParcel(Long parcelId) {
        if (!parcelRepository.existsById(parcelId)) {
            throw new ResourceNotFoundException("Parcelle", "id", parcelId);
        }
        return cropMapper.toResponseList(cropRepository.findByParcelId(parcelId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropResponse> getActiveCropsByParcel(Long parcelId) {
        if (!parcelRepository.existsById(parcelId)) {
            throw new ResourceNotFoundException("Parcelle", "id", parcelId);
        }
        return cropMapper.toResponseList(cropRepository.findByParcelIdAndIsActiveTrue(parcelId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropResponse> getCropsByFarm(Long farmId) {
        if (!farmRepository.existsById(farmId)) {
            throw new ResourceNotFoundException("Ferme", "id", farmId);
        }
        return cropMapper.toResponseList(cropRepository.findByFarmId(farmId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropResponse> getActiveCropsByFarm(Long farmId) {
        if (!farmRepository.existsById(farmId)) {
            throw new ResourceNotFoundException("Ferme", "id", farmId);
        }
        return cropMapper.toResponseList(cropRepository.findActiveByFarmId(farmId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropResponse> getCropsByType(CropType cropType) {
        return cropMapper.toResponseList(cropRepository.findByCropType(cropType));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropResponse> getCropsByGrowthStage(GrowthStage growthStage) {
        return cropMapper.toResponseList(cropRepository.findByGrowthStage(growthStage));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropResponse> getCropsReadyForHarvest(LocalDate startDate, LocalDate endDate) {
        return cropMapper.toResponseList(cropRepository.findByExpectedHarvestDateBetween(startDate, endDate));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CropResponse> getCropsReadyForHarvestByFarm(Long farmId, LocalDate startDate, LocalDate endDate) {
        if (!farmRepository.existsById(farmId)) {
            throw new ResourceNotFoundException("Ferme", "id", farmId);
        }
        return cropMapper.toResponseList(
                cropRepository.findByFarmIdAndExpectedHarvestDateBetween(farmId, startDate, endDate));
    }

    @Override
    public CropResponse updateCrop(Long id, CropRequest request) {
        log.info("Mise à jour de la culture: {}", id);

        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Culture", "id", id));

        if (!crop.getName().equals(request.getName()) && 
            cropRepository.existsByNameAndParcelId(request.getName(), crop.getParcel().getId())) {
            throw new BadRequestException("Une culture avec ce nom existe déjà dans cette parcelle");
        }

        cropMapper.updateEntity(request, crop);
        Crop updatedCrop = cropRepository.save(crop);

        log.info("Culture mise à jour avec succès: {}", updatedCrop.getId());
        return cropMapper.toResponse(updatedCrop);
    }

    @Override
    public CropResponse updateGrowthStage(Long id, GrowthStage stage) {
        log.info("Mise à jour du stade de croissance de la culture: {} vers: {}", id, stage);

        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Culture", "id", id));

        crop.setGrowthStage(stage);
        Crop updatedCrop = cropRepository.save(crop);

        return cropMapper.toResponse(updatedCrop);
    }

    @Override
    public CropResponse harvestCrop(Long id, LocalDate harvestDate) {
        log.info("Enregistrement de la récolte de la culture: {} à la date: {}", id, harvestDate);

        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Culture", "id", id));

        crop.setActualHarvestDate(harvestDate);
        crop.setGrowthStage(GrowthStage.HARVESTING);
        Crop updatedCrop = cropRepository.save(crop);

        return cropMapper.toResponse(updatedCrop);
    }

    @Override
    public CropResponse deactivateCrop(Long id) {
        log.info("Désactivation de la culture: {}", id);

        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Culture", "id", id));

        crop.setIsActive(false);
        crop.setGrowthStage(GrowthStage.DORMANT);
        Crop updatedCrop = cropRepository.save(crop);

        return cropMapper.toResponse(updatedCrop);
    }

    @Override
    @Transactional(readOnly = true)
    public long countCropsByParcel(Long parcelId) {
        return cropRepository.countByParcelId(parcelId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveCropsByParcel(Long parcelId) {
        return cropRepository.countByParcelIdAndIsActiveTrue(parcelId);
    }

    @Override
    public void deleteCrop(Long id) {
        log.info("Suppression de la culture: {}", id);

        Crop crop = cropRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Culture", "id", id));

        cropRepository.delete(crop);
        log.info("Culture supprimée avec succès: {}", id);
    }
}
