package org.project.projectstep1zanix.catalog.nearbyplace;

import java.util.List;

public interface NearbyPlaceService {
    NearbyPlaceResponseDto createNearbyPlace(Long hotelId, NearbyPlaceRequestDto dto);
    List<NearbyPlaceResponseDto> getNearbyPlacesByHotelId(Long hotelId);
    NearbyPlaceResponseDto updateNearbyPlace(Long hotelId, Long placeId, NearbyPlaceRequestDto dto);
    void deleteNearbyPlace(Long hotelId, Long placeId);
}
