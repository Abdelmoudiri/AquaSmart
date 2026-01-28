package com.aquasmart.farmservice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "farms")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "total_area")
    private Double totalArea; // en hectares

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FarmStatus status;

    @Column(name = "water_source")
    private String waterSource;

    @Column(name = "climate_zone")
    private String climateZone;

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Parcel> parcels = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper methods
    public void addParcel(Parcel parcel) {
        parcels.add(parcel);
        parcel.setFarm(this);
    }

    public void removeParcel(Parcel parcel) {
        parcels.remove(parcel);
        parcel.setFarm(null);
    }
}
