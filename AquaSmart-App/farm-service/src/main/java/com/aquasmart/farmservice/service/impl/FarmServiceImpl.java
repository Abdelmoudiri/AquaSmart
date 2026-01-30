package com.aquasmart.farmservice.service.impl;

import com.aquasmart.farmservice.dto.FarmRequest;
import com.aquasmart.farmservice.dto.FarmResponse;
import com.aquasmart.farmservice.exception.BadRequestException;
import com.aquasmart.farmservice.exception.ResourceNotFoundException;
import com.aquasmart.farmservice.mapper.FarmMapper;
import com.aquasmart.farmservice.mapper.ParcelMapper;
import com.aquasmart.farmservice.model.Farm;
import com.aquasmart.farmservice.model.FarmStatus;
import com.aquasmart.farmservice.repository.FarmRepository;
import com.aquasmart.farmservice.repository.ParcelRepository;
import com.aquasmart.farmservice.service.FarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FarmServiceImpl implements FarmService {

    private final FarmRepository farmRepository;
    private final ParcelRepository parcelRepository;
    private final FarmMapper farmMapper;
    private final ParcelMapper parcelMapper;

    @Override
    public FarmResponse createFarm(FarmRequest request, Long ownerId) {
        log.info("Création d'une nouvelle ferme pour l'utilisateur: {}", ownerId);

        if (farmRepository.existsByNameAndOwnerId(request.getName(), ownerId)) {
            throw new BadRequestException("Une ferme avec ce nom existe déjà pour cet utilisateur");
        }

        Farm farm = farmMapper.toEntity(request);
        farm.setOwnerId(ownerId);
        farm.setStatus(FarmStatus.UNDER_CONSTRUCTION);

        Farm savedFarm = farmRepository.save(farm);
        log.info("Ferme créée avec succès: {}", savedFarm.getId());

        return farmMapper.toResponse(savedFarm);
    }

    @Override
    @Transactional(readOnly = true)
    public FarmResponse getFarmById(Long id) {
        Farm farm = farmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ferme", "id", id));
        return farmMapper.toResponse(farm);
    }

    @Override
    @Transactional(readOnly = true)
    public FarmResponse getFarmByIdAndOwner(Long id, Long ownerId) {
        Farm farm = farmRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Ferme", "id", id));
        return farmMapper.toResponse(farm);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FarmResponse> getAllFarms() {
        return farmMapper.toResponseList(farmRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FarmResponse> getFarmsByOwner(Long ownerId) {
        return farmMapper.toResponseList(farmRepository.findByOwnerId(ownerId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FarmResponse> getFarmsByStatus(FarmStatus status) {
        return farmMapper.toResponseList(farmRepository.findByStatus(status));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FarmResponse> searchFarms(String keyword) {
        return farmMapper.toResponseList(farmRepository.searchByKeyword(keyword));
    }

    @Override
    public FarmResponse updateFarm(Long id, FarmRequest request, Long ownerId) {
        log.info("Mise à jour de la ferme: {} pour l'utilisateur: {}", id, ownerId);

        Farm farm = farmRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Ferme", "id", id));

        // Vérifier si le nouveau nom n'est pas déjà utilisé
        if (!farm.getName().equals(request.getName()) && 
            farmRepository.existsByNameAndOwnerId(request.getName(), ownerId)) {
            throw new BadRequestException("Une ferme avec ce nom existe déjà");
        }

        farmMapper.updateEntity(request, farm);
        Farm updatedFarm = farmRepository.save(farm);

        log.info("Ferme mise à jour avec succès: {}", updatedFarm.getId());
        return farmMapper.toResponse(updatedFarm);
    }

    @Override
    public FarmResponse updateFarmStatus(Long id, FarmStatus status, Long ownerId) {
        log.info("Mise à jour du statut de la ferme: {} vers: {}", id, status);

        Farm farm = farmRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Ferme", "id", id));

        farm.setStatus(status);
        Farm updatedFarm = farmRepository.save(farm);

        return farmMapper.toResponse(updatedFarm);
    }

    @Override
    public void deleteFarm(Long id, Long ownerId) {
        log.info("Suppression de la ferme: {} pour l'utilisateur: {}", id, ownerId);

        Farm farm = farmRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Ferme", "id", id));

        farmRepository.delete(farm);
        log.info("Ferme supprimée avec succès: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public FarmResponse getFarmWithParcels(Long id) {
        Farm farm = farmRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ferme", "id", id));

        FarmResponse response = farmMapper.toResponse(farm);
        response.setParcels(parcelMapper.toResponseList(parcelRepository.findByFarmId(id)));

        return response;
    }
}
