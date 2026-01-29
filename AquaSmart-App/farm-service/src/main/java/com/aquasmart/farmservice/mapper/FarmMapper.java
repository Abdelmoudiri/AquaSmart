package com.aquasmart.farmservice.mapper;

import com.aquasmart.farmservice.dto.FarmRequest;
import com.aquasmart.farmservice.dto.FarmResponse;
import com.aquasmart.farmservice.model.Farm;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", 
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {ParcelMapper.class})
public interface FarmMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "parcels", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Farm toEntity(FarmRequest request);

    @Named("toResponse")
    @Mapping(target = "parcels", ignore = true)
    @Mapping(target = "parcelCount", expression = "java(farm.getParcels() != null ? farm.getParcels().size() : 0)")
    FarmResponse toResponse(Farm farm);

    @IterableMapping(qualifiedByName = "toResponse")
    List<FarmResponse> toResponseList(List<Farm> farms);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "parcels", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(FarmRequest request, @MappingTarget Farm farm);
}
