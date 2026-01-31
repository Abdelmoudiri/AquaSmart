package com.aquasmart.farmservice.controller;

import com.aquasmart.farmservice.dto.ParcelRequest;
import com.aquasmart.farmservice.dto.ParcelResponse;
import com.aquasmart.farmservice.model.ParcelStatus;
import com.aquasmart.farmservice.model.SoilType;
import com.aquasmart.farmservice.service.ParcelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/{farmId}/parcels")
@RequiredArgsConstructor
@Slf4j
public class ParcelController {

    private final ParcelService parcelService;

    @PostMapping
    public ResponseEntity<ParcelResponse> createParcel(
            @PathVariable Long farmId,
            @Valid @RequestBody ParcelRequest request) {
        
        ParcelResponse response = parcelService.createParcel(farmId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParcelResponse> getParcelById(
            @PathVariable Long farmId,
            @PathVariable Long id) {
        
        return ResponseEntity.ok(parcelService.getParcelById(id));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<ParcelResponse> getParcelWithCrops(
            @PathVariable Long farmId,
            @PathVariable Long id) {
        
        log.info("Requête de récupération de la parcelle avec cultures: {} de la ferme: {}", id, farmId);
        return ResponseEntity.ok(parcelService.getParcelWithCrops(id));
    }

    @GetMapping
    public ResponseEntity<List<ParcelResponse>> getParcelsByFarm(@PathVariable Long farmId) {
        log.info("Requête de récupération des parcelles de la ferme: {}", farmId);
        return ResponseEntity.ok(parcelService.getParcelsByFarm(farmId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<ParcelResponse>> getParcelsByStatus(
            @PathVariable Long farmId,
            @PathVariable ParcelStatus status) {
        
        log.info("Requête de récupération des parcelles avec statut: {} de la ferme: {}", status, farmId);
        return ResponseEntity.ok(parcelService.getParcelsByFarmAndStatus(farmId, status));
    }

    @GetMapping("/soil/{soilType}")
    public ResponseEntity<List<ParcelResponse>> getParcelsBySoilType(
            @PathVariable Long farmId,
            @PathVariable SoilType soilType) {
        
        log.info("Requête de récupération des parcelles avec type de sol: {} de la ferme: {}", soilType, farmId);
        return ResponseEntity.ok(parcelService.getParcelsByFarmAndSoilType(farmId, soilType));
    }

    @GetMapping("/needs-irrigation")
    public ResponseEntity<List<ParcelResponse>> getParcelsNeedingIrrigation(@PathVariable Long farmId) {
        log.info("Requête de récupération des parcelles nécessitant irrigation de la ferme: {}", farmId);
        return ResponseEntity.ok(parcelService.getParcelsNeedingIrrigationByFarm(farmId));
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countParcels(@PathVariable Long farmId) {
        log.info("Requête de comptage des parcelles de la ferme: {}", farmId);
        return ResponseEntity.ok(parcelService.countParcelsByFarm(farmId));
    }

    @GetMapping("/total-area")
    public ResponseEntity<Double> getTotalArea(@PathVariable Long farmId) {
        log.info("Requête de calcul de la superficie totale de la ferme: {}", farmId);
        return ResponseEntity.ok(parcelService.getTotalAreaByFarm(farmId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParcelResponse> updateParcel(
            @PathVariable Long farmId,
            @PathVariable Long id,
            @Valid @RequestBody ParcelRequest request) {
        
        log.info("Requête de mise à jour de la parcelle: {} de la ferme: {}", id, farmId);
        return ResponseEntity.ok(parcelService.updateParcel(id, request));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ParcelResponse> updateParcelStatus(
            @PathVariable Long farmId,
            @PathVariable Long id,
            @RequestParam ParcelStatus status) {
        
        log.info("Requête de mise à jour du statut de la parcelle: {} vers: {}", id, status);
        return ResponseEntity.ok(parcelService.updateParcelStatus(id, status));
    }

    @PatchMapping("/{id}/moisture")
    public ResponseEntity<ParcelResponse> updateParcelMoisture(
            @PathVariable Long farmId,
            @PathVariable Long id,
            @RequestParam Double moisture) {
        
        log.info("Requête de mise à jour de l'humidité de la parcelle: {} à: {}%", id, moisture);
        return ResponseEntity.ok(parcelService.updateParcelMoisture(id, moisture));
    }

    @PostMapping("/{id}/irrigate")
    public ResponseEntity<ParcelResponse> recordIrrigation(
            @PathVariable Long farmId,
            @PathVariable Long id,
            @RequestParam Double amount) {
        
        log.info("Requête d'enregistrement d'irrigation pour la parcelle: {} - {} litres", id, amount);
        return ResponseEntity.ok(parcelService.recordIrrigation(id, amount));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParcel(
            @PathVariable Long farmId,
            @PathVariable Long id) {
        
        log.info("Requête de suppression de la parcelle: {} de la ferme: {}", id, farmId);
        parcelService.deleteParcel(id);
        return ResponseEntity.noContent().build();
    }
}
