package com.aquasmart.farmservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "crops")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Crop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Nom de la culture (Tomate, Blé, etc.)

    @Column(name = "variety")
    private String variety; // Variété spécifique

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parcel_id", nullable = false)
    private Parcel parcel;

    @Enumerated(EnumType.STRING)
    @Column(name = "crop_type", nullable = false)
    private CropType cropType;

    @Enumerated(EnumType.STRING)
    @Column(name = "growth_stage")
    private GrowthStage growthStage;

    @Column(name = "planting_date")
    private LocalDate plantingDate;

    @Column(name = "expected_harvest_date")
    private LocalDate expectedHarvestDate;

    @Column(name = "actual_harvest_date")
    private LocalDate actualHarvestDate;

    @Column(name = "water_requirement")
    private Double waterRequirement; // Besoin en eau (mm/jour)

    @Column(name = "optimal_temp_min")
    private Double optimalTempMin; // Température optimale min (°C)

    @Column(name = "optimal_temp_max")
    private Double optimalTempMax; // Température optimale max (°C)

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
