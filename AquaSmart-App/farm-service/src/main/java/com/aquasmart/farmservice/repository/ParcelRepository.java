package com.aquasmart.farmservice.repository;

import com.aquasmart.farmservice.model.Parcel;
import com.aquasmart.farmservice.model.ParcelStatus;
import com.aquasmart.farmservice.model.SoilType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParcelRepository extends JpaRepository<Parcel, Long> {

    List<Parcel> findByFarmId(Long farmId);

    List<Parcel> findByFarmIdAndStatus(Long farmId, ParcelStatus status);

    List<Parcel> findByFarmIdAndSoilType(Long farmId, SoilType soilType);

    List<Parcel> findByStatus(ParcelStatus status);

    long countByFarmId(Long farmId);

    boolean existsByNameAndFarmId(String name, Long farmId);

    @Query("SELECT SUM(p.area) FROM Parcel p WHERE p.farm.id = :farmId")
    Double getTotalAreaByFarmId(@Param("farmId") Long farmId);

    @Query("SELECT p FROM Parcel p WHERE p.currentMoisture < p.optimalMoistureMin")
    List<Parcel> findParcelsNeedingIrrigation();

    @Query("SELECT p FROM Parcel p WHERE p.farm.id = :farmId AND p.currentMoisture < p.optimalMoistureMin")
    List<Parcel> findParcelsNeedingIrrigationByFarm(@Param("farmId") Long farmId);

    @Query("SELECT p FROM Parcel p JOIN FETCH p.crops WHERE p.id = :id")
    Parcel findByIdWithCrops(@Param("id") Long id);
}
