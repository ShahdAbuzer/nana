package org.project.projectstep1zanix.catalog.RoomType;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.project.projectstep1zanix.catalog.Hotel.Hotel;
import org.project.projectstep1zanix.catalog.Hotel.HotelRepository;
import org.project.projectstep1zanix.common.PagedResponse;
import org.project.projectstep1zanix.file.FileStorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class RoomTypeServiceImplTest {

    @Mock private RoomTypeRepository roomTypeRepository;
    @Mock private HotelRepository hotelRepository;
    @Mock private RoomTypeMapper roomTypeMapper;
    @Mock private FileStorageService fileStorageService;

    @InjectMocks
    private RoomTypeServiceImpl roomTypeService;

    private RoomType roomType;
    private RoomTypeResponseDto responseDto;
    private Hotel hotel;

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.setId(1L);

        roomType = new RoomType();
        roomType.setId(1L);
        roomType.setName("Deluxe Suite");
        roomType.setCapacity(2);
        roomType.setBasePrice(150.0);
        roomType.setAmenities(List.of("TV", "BALCONY", "AIR_CONDITIONING"));
        roomType.setHotel(hotel);
        roomType.setTotalRooms(10);

        responseDto = new RoomTypeResponseDto();
        responseDto.setId(1L);
        responseDto.setName("Deluxe Suite");
        responseDto.setCapacity(2);
        responseDto.setBasePrice(150.0);
        responseDto.setAmenities(List.of("TV", "BALCONY", "AIR_CONDITIONING"));
    }

    @Test
    @DisplayName("getAllRoomTypes - with all filters")
    @SuppressWarnings("unchecked")
    void getAllRoomTypes_shouldApplyAllFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<RoomType> page = new PageImpl<>(List.of(roomType), pageable, 1);

        when(roomTypeRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(roomTypeMapper.toDto(any(RoomType.class))).thenReturn(responseDto);

        PagedResponse<RoomTypeResponseDto> result = roomTypeService.getAllRoomTypes(
                1L, "Deluxe", 2, 50.0, 200.0,
                List.of("TV", "BALCONY"), pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Deluxe Suite", result.getContent().get(0).getName());
    }

    @Test
    @DisplayName("getAllRoomTypes - no filters")
    @SuppressWarnings("unchecked")
    void getAllRoomTypes_shouldReturnAllWhenNoFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<RoomType> page = new PageImpl<>(List.of(roomType), pageable, 1);

        when(roomTypeRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);
        when(roomTypeMapper.toDto(any(RoomType.class))).thenReturn(responseDto);

        PagedResponse<RoomTypeResponseDto> result = roomTypeService.getAllRoomTypes(
                null, null, null, null, null, null, pageable);

        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("getRoomTypeById - success")
    void getRoomTypeById_shouldReturn() {
        when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(roomType));
        when(roomTypeMapper.toDto(roomType)).thenReturn(responseDto);

        RoomTypeResponseDto result = roomTypeService.getRoomTypeById(1L);

        assertEquals("Deluxe Suite", result.getName());
    }

    @Test
    @DisplayName("getRoomTypeById - not found")
    void getRoomTypeById_shouldThrowWhenNotFound() {
        when(roomTypeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RoomTypeNotFoundException.class,
                () -> roomTypeService.getRoomTypeById(99L));
    }

    @Test
    @DisplayName("deleteRoomType - success")
    void deleteRoomType_shouldDelete() {
        when(roomTypeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(roomTypeRepository).deleteById(1L);

        roomTypeService.deleteRoomType(1L);

        verify(roomTypeRepository).deleteById(1L);
    }

    @Test
    @DisplayName("deleteRoomType - not found")
    void deleteRoomType_shouldThrowWhenNotFound() {
        when(roomTypeRepository.existsById(99L)).thenReturn(false);

        assertThrows(RoomTypeNotFoundException.class,
                () -> roomTypeService.deleteRoomType(99L));
    }
}
