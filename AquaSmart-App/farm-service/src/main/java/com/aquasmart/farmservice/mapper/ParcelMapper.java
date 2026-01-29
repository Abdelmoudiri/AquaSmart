package com.aquasmart.farmservice.mapper;

import com.aquasmart.farmservice.dto.ParcelRequest;
import com.aquasmart.farmservice.dto.ParcelResponse;
import com.aquasmart.farmservice.model.Parcel;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {CropMapper.class})
public interface ParcelMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "farm", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "currentMoisture", ignore = true)
    @Mapping(target = "lastIrrigationDate", ignore = true)
    @Mapping(target = "lastIrrigationAmount", ignore = true)
    @Mapping(target = "crops", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Parcel toEntity(ParcelRequest request);

    @Named("toResponseWithCrops")
    @Mapping(target = "farmId", source = "farm.id")
    @Mapping(target = "farmName", source = "farm.name")
    ParcelResponse toResponse(Parcel parcel);

    @Named("toResponseWithoutCrops")
    @Mapping(target = "farmId", source = "farm.id")
    @Mapping(target = "farmName", source = "farm.name")
    @Mapping(target = "crops", ignore = true)
    ParcelResponse toResponseWithoutCrops(Parcel parcel);

    @IterableMapping(qualifiedByName = "toResponseWithoutCrops")
    List<ParcelResponse> toResponseList(List<Parcel> parcels);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "farm", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "currentMoisture", ignore = true)
    @Mapping(target = "lastIrrigationDate", ignore = true)
    @Mapping(target = "lastIrrigationAmount", ignore = true)
    @Mapping(target = "crops", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(ParcelRequest request, @MappingTarget Parcel parcel);
}
