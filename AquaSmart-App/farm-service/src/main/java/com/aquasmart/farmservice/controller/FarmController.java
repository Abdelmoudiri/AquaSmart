package com.aquasmart.farmservice.controller;

import com.aquasmart.farmservice.dto.FarmRequest;
import com.aquasmart.farmservice.dto.FarmResponse;
import com.aquasmart.farmservice.model.FarmStatus;
import com.aquasmart.farmservice.service.FarmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class FarmController {

    private final FarmService farmService;

    @PostMapping
    public ResponseEntity<FarmResponse> createFarm(
            @Valid @RequestBody FarmRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        // Pour le développement, utiliser un ID par défaut si non fourni
        Long ownerId = userId != null ? userId : 1L;
        log.info("Requête de création de ferme reçue pour l'utilisateur: {}", ownerId);
        
        FarmResponse response = farmService.createFarm(request, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FarmResponse> getFarmById(@PathVariable Long id) {
        log.info("Requête de récupération de la ferme: {}", id);
        return ResponseEntity.ok(farmService.getFarmById(id));
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<FarmResponse> getFarmWithParcels(@PathVariable Long id) {
        log.info("Requête de récupération de la ferme avec parcelles: {}", id);
        return ResponseEntity.ok(farmService.getFarmWithParcels(id));
    }

    @GetMapping
    public ResponseEntity<List<FarmResponse>> getAllFarms() {
        log.info("Requête de récupération de toutes les fermes");
        return ResponseEntity.ok(farmService.getAllFarms());
    }

    @GetMapping("/my-farms")
    public ResponseEntity<List<FarmResponse>> getMyFarms(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        Long ownerId = userId != null ? userId : 1L;
        log.info("Requête de récupération des fermes de l'utilisateur: {}", ownerId);
        return ResponseEntity.ok(farmService.getFarmsByOwner(ownerId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<FarmResponse>> getFarmsByStatus(@PathVariable FarmStatus status) {
        log.info("Requête de récupération des fermes par statut: {}", status);
        return ResponseEntity.ok(farmService.getFarmsByStatus(status));
    }

    @GetMapping("/search")
    public ResponseEntity<List<FarmResponse>> searchFarms(@RequestParam String keyword) {
        log.info("Recherche de fermes avec le mot-clé: {}", keyword);
        return ResponseEntity.ok(farmService.searchFarms(keyword));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FarmResponse> updateFarm(
            @PathVariable Long id,
            @Valid @RequestBody FarmRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        Long ownerId = userId != null ? userId : 1L;
        log.info("Requête de mise à jour de la ferme: {} pour l'utilisateur: {}", id, ownerId);
        
        return ResponseEntity.ok(farmService.updateFarm(id, request, ownerId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<FarmResponse> updateFarmStatus(
            @PathVariable Long id,
            @RequestParam FarmStatus status,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        Long ownerId = userId != null ? userId : 1L;
        log.info("Requête de mise à jour du statut de la ferme: {} vers: {}", id, status);
        
        return ResponseEntity.ok(farmService.updateFarmStatus(id, status, ownerId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFarm(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        
        Long ownerId = userId != null ? userId : 1L;
        log.info("Requête de suppression de la ferme: {} pour l'utilisateur: {}", id, ownerId);
        
        farmService.deleteFarm(id, ownerId);
        return ResponseEntity.noContent().build();
    }
}
