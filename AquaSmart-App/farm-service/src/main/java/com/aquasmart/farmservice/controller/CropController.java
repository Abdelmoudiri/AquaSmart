package com.aquasmart.farmservice.controller;

import com.aquasmart.farmservice.dto.CropRequest;
import com.aquasmart.farmservice.dto.CropResponse;
import com.aquasmart.farmservice.model.CropType;
import com.aquasmart.farmservice.model.GrowthStage;
import com.aquasmart.farmservice.service.CropService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/{farmId}/parcels/{parcelId}/crops")
@RequiredArgsConstructor
@Slf4j
public class CropController {

    private final CropService cropService;

    @PostMapping
    public ResponseEntity<CropResponse> createCrop(
            @PathVariable Long farmId,
            @PathVariable Long parcelId,
            @Valid @RequestBody CropRequest request) {
        
        log.info("Requête de création de culture pour la parcelle: {}", parcelId);
        CropResponse response = cropService.createCrop(parcelId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CropResponse> getCropById(
            @PathVariable Long farmId,
            @PathVariable Long parcelId,
            @PathVariable Long id) {
        
        log.info("Requête de récupération de la culture: {}", id);
        return ResponseEntity.ok(cropService.getCropById(id));
    }

    @GetMapping
    public ResponseEntity<List<CropResponse>> getCropsByParcel(
            @PathVariable Long farmId,
            @PathVariable Long parcelId,
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly) {
        
        log.info("Requête de récupération des cultures de la parcelle: {}", parcelId);
        if (activeOnly) {
            return ResponseEntity.ok(cropService.getActiveCropsByParcel(parcelId));
        }
        return ResponseEntity.ok(cropService.getCropsByParcel(parcelId));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countCrops(
            @PathVariable Long farmId,
            @PathVariable Long parcelId,
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly) {
        
        log.info("Requête de comptage des cultures de la parcelle: {}", parcelId);
        if (activeOnly) {
            return ResponseEntity.ok(cropService.countActiveCropsByParcel(parcelId));
        }
        return ResponseEntity.ok(cropService.countCropsByParcel(parcelId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CropResponse> updateCrop(
            @PathVariable Long farmId,
            @PathVariable Long parcelId,
            @PathVariable Long id,
            @Valid @RequestBody CropRequest request) {
        
        log.info("Requête de mise à jour de la culture: {}", id);
        return ResponseEntity.ok(cropService.updateCrop(id, request));
    }

    @PatchMapping("/{id}/growth-stage")
    public ResponseEntity<CropResponse> updateGrowthStage(
            @PathVariable Long farmId,
            @PathVariable Long parcelId,
            @PathVariable Long id,
            @RequestParam GrowthStage stage) {
        
        log.info("Requête de mise à jour du stade de croissance de la culture: {} vers: {}", id, stage);
        return ResponseEntity.ok(cropService.updateGrowthStage(id, stage));
    }

    @PostMapping("/{id}/harvest")
    public ResponseEntity<CropResponse> harvestCrop(
            @PathVariable Long farmId,
            @PathVariable Long parcelId,
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate harvestDate) {
        
        log.info("Requête d'enregistrement de récolte pour la culture: {} à la date: {}", id, harvestDate);
        return ResponseEntity.ok(cropService.harvestCrop(id, harvestDate));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<CropResponse> deactivateCrop(
            @PathVariable Long farmId,
            @PathVariable Long parcelId,
            @PathVariable Long id) {
        
        log.info("Requête de désactivation de la culture: {}", id);
        return ResponseEntity.ok(cropService.deactivateCrop(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCrop(
            @PathVariable Long farmId,
            @PathVariable Long parcelId,
            @PathVariable Long id) {
        
        log.info("Requête de suppression de la culture: {}", id);
        cropService.deleteCrop(id);
        return ResponseEntity.noContent().build();
    }
}
