package com.aquasmart.irrigationservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Water Usage Record - Historique de consommation d'eau
 */
@Entity
@Table(name = "water_usage_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaterUsageRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long farmId;
    
    private Long parcelId;
    
    private Long eventId;
    
    /**
     * Date de l'enregistrement
     */
    @Column(nullable = false)
    private LocalDateTime recordDate;
    
    /**
     * Quantité d'eau utilisée (litres)
     */
    @Column(nullable = false)
    private Double waterUsed;
    
    /**
     * Quantité d'eau économisée (litres)
     */
    @Builder.Default
    private Double waterSaved = 0.0;
    
    /**
     * Coût estimé (MAD)
     */
    private Double estimatedCost;
    
    /**
     * Économie estimée (MAD)
     */
    private Double estimatedSavings;
    
    /**
     * Source de l'utilisation
     */
    @Enumerated(EnumType.STRING)
    private IrrigationSource source;
    
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
