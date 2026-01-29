package com.aquasmart.farmservice.dto;

import com.aquasmart.farmservice.model.FarmStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmResponse {

    private Long id;
    private String name;
    private String description;
    private String location;
    private Double latitude;
    private Double longitude;
    private Long ownerId;
    private Double totalArea; // en hectares
    private FarmStatus status;
    private String waterSource;
    private String climateZone;
    private Integer parcelCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ParcelResponse> parcels;
}
