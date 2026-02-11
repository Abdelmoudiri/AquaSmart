package com.aquasmart.irrigationservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Irrigation Event - Événement d'irrigation (exécution réelle)
 */
@Entity
@Table(name = "irrigation_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IrrigationEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    @ToString.Exclude
    private IrrigationSchedule schedule;
    
    @Column(nullable = false)
    private Long parcelId;
    
    @Column(nullable = false)
    private Long farmId;
    
    /**
     * Source de déclenchement
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IrrigationSource source;
    
    /**
     * Statut de l'événement
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private IrrigationStatus status = IrrigationStatus.SCHEDULED;
    
    /**
     * Heure de début programmée
     */
    @Column(nullable = false)
    private LocalDateTime scheduledStartTime;
    
    /**
     * Heure de fin programmée
     */
    @Column(nullable = false)
    private LocalDateTime scheduledEndTime;
    
    /**
     * Heure de début réelle
     */
    private LocalDateTime actualStartTime;
    
    /**
     * Heure de fin réelle
     */
    private LocalDateTime actualEndTime;
    
    /**
     * Quantité d'eau prévue (litres)
     */
    @Column(nullable = false)
    private Double plannedWaterAmount;
    
    /**
     * Quantité d'eau réellement utilisée (litres)
     */
    private Double actualWaterAmount;
    
    /**
     * Humidité du sol avant irrigation
     */
    private Double soilMoistureBefore;
    
    /**
     * Humidité du sol après irrigation
     */
    private Double soilMoistureAfter;
    
    /**
     * Température au moment de l'irrigation
     */
    private Double temperature;
    
    /**
     * Humidité de l'air au moment de l'irrigation
     */
    private Double humidity;
    
    /**
     * Vitesse du vent (km/h)
     */
    private Double windSpeed;
    
    /**
     * Raison de l'annulation ou du saut
     */
    private String skipReason;
    
    /**
     * Notes additionnelles
     */
    private String notes;
    
    /**
     * Économie d'eau réalisée (litres)
     */
    private Double waterSaved;
    
    /**
     * Score d'efficacité (0-100)
     */
    private Integer efficiencyScore;
    
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
