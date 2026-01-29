package com.aquasmart.farmservice.repository;

import com.aquasmart.farmservice.model.Farm;
import com.aquasmart.farmservice.model.FarmStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmRepository extends JpaRepository<Farm, Long> {

    List<Farm> findByOwnerId(Long ownerId);

    List<Farm> findByStatus(FarmStatus status);

    List<Farm> findByOwnerIdAndStatus(Long ownerId, FarmStatus status);

    Optional<Farm> findByIdAndOwnerId(Long id, Long ownerId);

    @Query("SELECT f FROM Farm f WHERE f.location LIKE %:location%")
    List<Farm> findByLocationContaining(@Param("location") String location);

    @Query("SELECT f FROM Farm f WHERE f.name LIKE %:keyword% OR f.description LIKE %:keyword%")
    List<Farm> searchByKeyword(@Param("keyword") String keyword);

    boolean existsByNameAndOwnerId(String name, Long ownerId);
}
