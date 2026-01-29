package com.aquasmart.farmservice.repository;

import com.aquasmart.farmservice.model.Crop;
import com.aquasmart.farmservice.model.CropType;
import com.aquasmart.farmservice.model.GrowthStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {

    List<Crop> findByParcelId(Long parcelId);

    List<Crop> findByParcelIdAndIsActiveTrue(Long parcelId);

    List<Crop> findByCropType(CropType cropType);

    List<Crop> findByGrowthStage(GrowthStage growthStage);

    List<Crop> findByParcelIdAndCropType(Long parcelId, CropType cropType);

    long countByParcelId(Long parcelId);

    long countByParcelIdAndIsActiveTrue(Long parcelId);

    boolean existsByNameAndParcelId(String name, Long parcelId);

    @Query("SELECT c FROM Crop c WHERE c.parcel.farm.id = :farmId")
    List<Crop> findByFarmId(@Param("farmId") Long farmId);

    @Query("SELECT c FROM Crop c WHERE c.parcel.farm.id = :farmId AND c.isActive = true")
    List<Crop> findActiveByFarmId(@Param("farmId") Long farmId);

    @Query("SELECT c FROM Crop c WHERE c.expectedHarvestDate BETWEEN :startDate AND :endDate")
    List<Crop> findByExpectedHarvestDateBetween(
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);

    @Query("SELECT c FROM Crop c WHERE c.parcel.farm.id = :farmId AND c.expectedHarvestDate BETWEEN :startDate AND :endDate")
    List<Crop> findByFarmIdAndExpectedHarvestDateBetween(
            @Param("farmId") Long farmId,
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
}
