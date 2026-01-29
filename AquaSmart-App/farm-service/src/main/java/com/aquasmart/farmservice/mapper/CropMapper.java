package com.aquasmart.farmservice.mapper;

import com.aquasmart.farmservice.dto.CropRequest;
import com.aquasmart.farmservice.dto.CropResponse;
import com.aquasmart.farmservice.model.Crop;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CropMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parcel", ignore = true)
    @Mapping(target = "actualHarvestDate", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Crop toEntity(CropRequest request);

    @Mapping(target = "parcelId", source = "parcel.id")
    @Mapping(target = "parcelName", source = "parcel.name")
    CropResponse toResponse(Crop crop);

    List<CropResponse> toResponseList(List<Crop> crops);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parcel", ignore = true)
    @Mapping(target = "actualHarvestDate", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(CropRequest request, @MappingTarget Crop crop);
}
