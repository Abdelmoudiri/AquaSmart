package com.aquasmart.farmservice.service.impl;

import com.aquasmart.farmservice.dto.ParcelRequest;
import com.aquasmart.farmservice.dto.ParcelResponse;
import com.aquasmart.farmservice.exception.BadRequestException;
import com.aquasmart.farmservice.exception.ResourceNotFoundException;
import com.aquasmart.farmservice.mapper.ParcelMapper;
import com.aquasmart.farmservice.model.Farm;
import com.aquasmart.farmservice.model.Parcel;
import com.aquasmart.farmservice.model.ParcelStatus;
import com.aquasmart.farmservice.model.SoilType;
import com.aquasmart.farmservice.repository.FarmRepository;
import com.aquasmart.farmservice.repository.ParcelRepository;
import com.aquasmart.farmservice.service.ParcelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ParcelServiceImpl implements ParcelService {

    private final ParcelRepository parcelRepository;
    private final FarmRepository farmRepository;
    private final ParcelMapper parcelMapper;

    @Override
    public ParcelResponse createParcel(Long farmId, ParcelRequest request) {
        log.info("Création d'une nouvelle parcelle pour la ferme: {}", farmId);

        Farm farm = farmRepository.findById(farmId)
                .orElseThrow(() -> new ResourceNotFoundException("Ferme", "id", farmId));

        if (parcelRepository.existsByNameAndFarmId(request.getName(), farmId)) {
            throw new BadRequestException("Une parcelle avec ce nom existe déjà dans cette ferme");
        }

        Parcel parcel = parcelMapper.toEntity(request);
        parcel.setFarm(farm);
        parcel.setStatus(ParcelStatus.PREPARATION);

        Parcel savedParcel = parcelRepository.save(parcel);
        log.info("Parcelle créée avec succès: {}", savedParcel.getId());

        return parcelMapper.toResponseWithoutCrops(savedParcel);
    }

    @Override
    @Transactional(readOnly = true)
    public ParcelResponse getParcelById(Long id) {
        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcelle", "id", id));
        return parcelMapper.toResponseWithoutCrops(parcel);
    }

    @Override
    @Transactional(readOnly = true)
    public ParcelResponse getParcelWithCrops(Long id) {
        Parcel parcel = parcelRepository.findByIdWithCrops(id);
        if (parcel == null) {
            throw new ResourceNotFoundException("Parcelle", "id", id);
        }
        return parcelMapper.toResponse(parcel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParcelResponse> getParcelsByFarm(Long farmId) {
        if (!farmRepository.existsById(farmId)) {
            throw new ResourceNotFoundException("Ferme", "id", farmId);
        }
        return parcelMapper.toResponseList(parcelRepository.findByFarmId(farmId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParcelResponse> getParcelsByFarmAndStatus(Long farmId, ParcelStatus status) {
        if (!farmRepository.existsById(farmId)) {
            throw new ResourceNotFoundException("Ferme", "id", farmId);
        }
        return parcelMapper.toResponseList(parcelRepository.findByFarmIdAndStatus(farmId, status));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParcelResponse> getParcelsByFarmAndSoilType(Long farmId, SoilType soilType) {
        if (!farmRepository.existsById(farmId)) {
            throw new ResourceNotFoundException("Ferme", "id", farmId);
        }
        return parcelMapper.toResponseList(parcelRepository.findByFarmIdAndSoilType(farmId, soilType));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParcelResponse> getParcelsNeedingIrrigation() {
        return parcelMapper.toResponseList(parcelRepository.findParcelsNeedingIrrigation());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParcelResponse> getParcelsNeedingIrrigationByFarm(Long farmId) {
        if (!farmRepository.existsById(farmId)) {
            throw new ResourceNotFoundException("Ferme", "id", farmId);
        }
        return parcelMapper.toResponseList(parcelRepository.findParcelsNeedingIrrigationByFarm(farmId));
    }

    @Override
    public ParcelResponse updateParcel(Long id, ParcelRequest request) {
        log.info("Mise à jour de la parcelle: {}", id);

        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcelle", "id", id));

        if (!parcel.getName().equals(request.getName()) && 
            parcelRepository.existsByNameAndFarmId(request.getName(), parcel.getFarm().getId())) {
            throw new BadRequestException("Une parcelle avec ce nom existe déjà dans cette ferme");
        }

        parcelMapper.updateEntity(request, parcel);
        Parcel updatedParcel = parcelRepository.save(parcel);

        log.info("Parcelle mise à jour avec succès: {}", updatedParcel.getId());
        return parcelMapper.toResponseWithoutCrops(updatedParcel);
    }

    @Override
    public ParcelResponse updateParcelStatus(Long id, ParcelStatus status) {
        log.info("Mise à jour du statut de la parcelle: {} vers: {}", id, status);

        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcelle", "id", id));

        parcel.setStatus(status);
        Parcel updatedParcel = parcelRepository.save(parcel);

        return parcelMapper.toResponseWithoutCrops(updatedParcel);
    }

    @Override
    public ParcelResponse updateParcelMoisture(Long id, Double moisture) {
        log.info("Mise à jour de l'humidité de la parcelle: {} à: {}%", id, moisture);

        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcelle", "id", id));

        if (moisture < 0 || moisture > 100) {
            throw new BadRequestException("L'humidité doit être entre 0 et 100%");
        }

        parcel.setCurrentMoisture(moisture);
        Parcel updatedParcel = parcelRepository.save(parcel);

        return parcelMapper.toResponseWithoutCrops(updatedParcel);
    }

    @Override
    public ParcelResponse recordIrrigation(Long id, Double amount) {
        log.info("Enregistrement d'une irrigation pour la parcelle: {} - {} litres", id, amount);

        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcelle", "id", id));

        if (amount <= 0) {
            throw new BadRequestException("La quantité d'eau doit être positive");
        }

        parcel.setLastIrrigationDate(LocalDateTime.now());
        parcel.setLastIrrigationAmount(amount);
        
        Parcel updatedParcel = parcelRepository.save(parcel);

        return parcelMapper.toResponseWithoutCrops(updatedParcel);
    }

    @Override
    @Transactional(readOnly = true)
    public long countParcelsByFarm(Long farmId) {
        return parcelRepository.countByFarmId(farmId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getTotalAreaByFarm(Long farmId) {
        if (!farmRepository.existsById(farmId)) {
            throw new ResourceNotFoundException("Ferme", "id", farmId);
        }
        Double totalArea = parcelRepository.getTotalAreaByFarmId(farmId);
        return totalArea != null ? totalArea : 0.0;
    }

    @Override
    public void deleteParcel(Long id) {
        log.info("Suppression de la parcelle: {}", id);

        Parcel parcel = parcelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parcelle", "id", id));

        parcelRepository.delete(parcel);
        log.info("Parcelle supprimée avec succès: {}", id);
    }
}
