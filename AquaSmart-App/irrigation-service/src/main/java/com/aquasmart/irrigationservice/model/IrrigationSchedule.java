package com.aquasmart.irrigationservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Irrigation Schedule - Planning d'irrigation pour une parcelle
 */
@Entity
@Table(name = "irrigation_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IrrigationSchedule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long parcelId;
    
    @Column(nullable = false)
    private Long farmId;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    /**
     * Type d'irrigation (DRIP, SPRINKLER, etc.)
     */
    @Column(nullable = false)
    private String irrigationType;
    
    /**
     * Heure de début programmée
     */
    @Column(nullable = false)
    private LocalTime startTime;
    
    /**
     * Durée en minutes
     */
    @Column(nullable = false)
    private Integer durationMinutes;
    
    /**
     * Quantité d'eau en litres par m²
     */
    @Column(nullable = false)
    private Double waterAmountPerSquareMeter;
    
    /**
     * Jours de la semaine actifs (format: "1,2,3,4,5" pour lun-ven)
     */
    private String activeDays;
    
    /**
     * Actif ou non
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
    
    /**
     * Utiliser les données météo pour optimiser
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean weatherAware = true;
    
    /**
     * Utiliser les données des capteurs pour optimiser
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean sensorAware = true;
    
    /**
     * Seuil d'humidité minimum pour déclencher l'irrigation
     */
    private Double minSoilMoistureThreshold;
    
    /**
     * Seuil d'humidité maximum pour arrêter l'irrigation
     */
    private Double maxSoilMoistureThreshold;
    
    /**
     * Priorité de l'irrigation
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private IrrigationPriority priority = IrrigationPriority.NORMAL;
    
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<IrrigationEvent> events = new ArrayList<>();
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
