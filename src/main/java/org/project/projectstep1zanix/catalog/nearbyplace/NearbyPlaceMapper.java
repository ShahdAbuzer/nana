package org.project.projectstep1zanix.catalog.nearbyplace;

import org.springframework.stereotype.Component;

@Component
public class NearbyPlaceMapper {

    public NearbyPlace toEntity(NearbyPlaceRequestDto dto, Long hotelId) {
        NearbyPlace entity = new NearbyPlace();
        entity.setHotelId(hotelId);
        entity.setName(dto.getName());
        entity.setType(dto.getType());
        entity.setDistanceKm(dto.getDistanceKm());
        entity.setDescription(dto.getDescription());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        return entity;
    }

    public NearbyPlaceResponseDto toDto(NearbyPlace entity) {
        NearbyPlaceResponseDto dto = new NearbyPlaceResponseDto();
        dto.setId(entity.getId());
        dto.setHotelId(entity.getHotelId());
        dto.setName(entity.getName());
        dto.setType(entity.getType());
        dto.setDistanceKm(entity.getDistanceKm());
        dto.setDescription(entity.getDescription());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        return dto;
    }

    public void updateEntityFromDto(NearbyPlaceRequestDto dto, NearbyPlace entity) {
        entity.setName(dto.getName());
        entity.setType(dto.getType());
        entity.setDistanceKm(dto.getDistanceKm());
        entity.setDescription(dto.getDescription());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
    }
}
