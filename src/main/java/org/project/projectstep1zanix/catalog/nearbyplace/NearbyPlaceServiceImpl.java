package org.project.projectstep1zanix.catalog.nearbyplace;

import java.util.List;

import org.project.projectstep1zanix.catalog.Hotel.HotelRepository;
import org.project.projectstep1zanix.catalog.Hotel.HotelNotFoundException;
import org.project.projectstep1zanix.common.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NearbyPlaceServiceImpl implements NearbyPlaceService {

    private final NearbyPlaceRepository nearbyPlaceRepository;
    private final HotelRepository hotelRepository;
    private final NearbyPlaceMapper nearbyPlaceMapper;

    public NearbyPlaceServiceImpl(NearbyPlaceRepository nearbyPlaceRepository,
                                  HotelRepository hotelRepository,
                                  NearbyPlaceMapper nearbyPlaceMapper) {
        this.nearbyPlaceRepository = nearbyPlaceRepository;
        this.hotelRepository = hotelRepository;
        this.nearbyPlaceMapper = nearbyPlaceMapper;
    }

    @Override
    @Transactional
    public NearbyPlaceResponseDto createNearbyPlace(Long hotelId, NearbyPlaceRequestDto dto) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new HotelNotFoundException(hotelId);
        }
        NearbyPlace entity = nearbyPlaceMapper.toEntity(dto, hotelId);
        NearbyPlace saved = nearbyPlaceRepository.save(entity);
        return nearbyPlaceMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NearbyPlaceResponseDto> getNearbyPlacesByHotelId(Long hotelId) {
        return nearbyPlaceRepository.findByHotelId(hotelId).stream()
                .map(nearbyPlaceMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public NearbyPlaceResponseDto updateNearbyPlace(Long hotelId, Long placeId, NearbyPlaceRequestDto dto) {
        NearbyPlace place = nearbyPlaceRepository.findById(placeId)
                .orElseThrow(() -> new ResourceNotFoundException("Nearby place not found with id: " + placeId));
        if (!place.getHotelId().equals(hotelId)) {
            throw new ResourceNotFoundException("Nearby place not found for hotel: " + hotelId);
        }
        nearbyPlaceMapper.updateEntityFromDto(dto, place);
        NearbyPlace updated = nearbyPlaceRepository.save(place);
        return nearbyPlaceMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteNearbyPlace(Long hotelId, Long placeId) {
        NearbyPlace place = nearbyPlaceRepository.findById(placeId)
                .orElseThrow(() -> new ResourceNotFoundException("Nearby place not found with id: " + placeId));
        if (!place.getHotelId().equals(hotelId)) {
            throw new ResourceNotFoundException("Nearby place not found for hotel: " + hotelId);
        }
        nearbyPlaceRepository.delete(place);
    }
}
