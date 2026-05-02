package org.project.projectstep1zanix.catalog.nearbyplace;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.projectstep1zanix.catalog.Hotel.HotelRepository;
import org.project.projectstep1zanix.catalog.Hotel.HotelNotFoundException;
import org.project.projectstep1zanix.common.ResourceNotFoundException;

@ExtendWith(MockitoExtension.class)
class NearbyPlaceServiceImplTest {

    @Mock private NearbyPlaceRepository nearbyPlaceRepository;
    @Mock private HotelRepository hotelRepository;
    @Mock private NearbyPlaceMapper nearbyPlaceMapper;

    @InjectMocks
    private NearbyPlaceServiceImpl service;

    private NearbyPlace place;
    private NearbyPlaceRequestDto requestDto;
    private NearbyPlaceResponseDto responseDto;

    @BeforeEach
    void setUp() {
        place = new NearbyPlace();
        place.setId(1L);
        place.setHotelId(1L);
        place.setName("Roman Amphitheatre");
        place.setType("Historical");
        place.setDistanceKm(2.5);
        place.setDescription("Ancient Roman theatre");

        requestDto = new NearbyPlaceRequestDto();
        requestDto.setName("Roman Amphitheatre");
        requestDto.setType("Historical");
        requestDto.setDistanceKm(2.5);
        requestDto.setDescription("Ancient Roman theatre");

        responseDto = new NearbyPlaceResponseDto();
        responseDto.setId(1L);
        responseDto.setHotelId(1L);
        responseDto.setName("Roman Amphitheatre");
    }

    @Test
    @DisplayName("create - success")
    void create_shouldSaveAndReturn() {
        when(hotelRepository.existsById(1L)).thenReturn(true);
        when(nearbyPlaceMapper.toEntity(requestDto, 1L)).thenReturn(place);
        when(nearbyPlaceRepository.save(place)).thenReturn(place);
        when(nearbyPlaceMapper.toDto(place)).thenReturn(responseDto);

        NearbyPlaceResponseDto result = service.createNearbyPlace(1L, requestDto);

        assertNotNull(result);
        assertEquals("Roman Amphitheatre", result.getName());
        verify(nearbyPlaceRepository).save(place);
    }

    @Test
    @DisplayName("create - hotel not found")
    void create_shouldThrowWhenHotelNotFound() {
        when(hotelRepository.existsById(99L)).thenReturn(false);

        assertThrows(HotelNotFoundException.class,
                () -> service.createNearbyPlace(99L, requestDto));
    }

    @Test
    @DisplayName("getByHotelId - returns list")
    void getByHotelId_shouldReturnList() {
        when(nearbyPlaceRepository.findByHotelId(1L)).thenReturn(List.of(place));
        when(nearbyPlaceMapper.toDto(place)).thenReturn(responseDto);

        List<NearbyPlaceResponseDto> result = service.getNearbyPlacesByHotelId(1L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("update - success")
    void update_shouldUpdateAndReturn() {
        when(nearbyPlaceRepository.findById(1L)).thenReturn(Optional.of(place));
        doNothing().when(nearbyPlaceMapper).updateEntityFromDto(requestDto, place);
        when(nearbyPlaceRepository.save(place)).thenReturn(place);
        when(nearbyPlaceMapper.toDto(place)).thenReturn(responseDto);

        NearbyPlaceResponseDto result = service.updateNearbyPlace(1L, 1L, requestDto);

        assertNotNull(result);
        verify(nearbyPlaceMapper).updateEntityFromDto(requestDto, place);
    }

    @Test
    @DisplayName("update - wrong hotel")
    void update_shouldThrowWhenWrongHotel() {
        when(nearbyPlaceRepository.findById(1L)).thenReturn(Optional.of(place));

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateNearbyPlace(99L, 1L, requestDto));
    }

    @Test
    @DisplayName("delete - success")
    void delete_shouldRemove() {
        when(nearbyPlaceRepository.findById(1L)).thenReturn(Optional.of(place));
        doNothing().when(nearbyPlaceRepository).delete(place);

        service.deleteNearbyPlace(1L, 1L);

        verify(nearbyPlaceRepository).delete(place);
    }

    @Test
    @DisplayName("delete - not found")
    void delete_shouldThrowWhenNotFound() {
        when(nearbyPlaceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.deleteNearbyPlace(1L, 99L));
    }
}
