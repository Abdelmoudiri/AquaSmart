package com.aquasmart.farmservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parcels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parcel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @Column(nullable = false)
    private Double area; // en hectares

    @Enumerated(EnumType.STRING)
    @Column(name = "soil_type", nullable = false)
    private SoilType soilType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParcelStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "irrigation_type")
    private IrrigationType irrigationType;

    @Column(name = "optimal_moisture_min")
    private Double optimalMoistureMin; // % humidité minimum

    @Column(name = "optimal_moisture_max")
    private Double optimalMoistureMax; // % humidité maximum

    @Column(name = "current_moisture")
    private Double currentMoisture; // % humidité actuelle (via capteur)

    @Column(name = "last_irrigation_date")
    private LocalDateTime lastIrrigationDate;

    @Column(name = "last_irrigation_amount")
    private Double lastIrrigationAmount; // en litres

    @OneToMany(mappedBy = "parcel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Crop> crops = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper methods
    public void addCrop(Crop crop) {
        crops.add(crop);
        crop.setParcel(this);
    }

    public void removeCrop(Crop crop) {
        crops.remove(crop);
        crop.setParcel(null);
    }
}
