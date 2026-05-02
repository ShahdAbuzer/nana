package org.project.projectstep1zanix.catalog.Hotel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.projectstep1zanix.Users.AppUser;
import org.project.projectstep1zanix.Users.AppUserRepository;
import org.project.projectstep1zanix.Users.ManagerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class HotelServiceImplTest {

    @Mock
    private HotelRepository hotelRepository;
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private ManagerRepository managerRepository;

    @InjectMocks
    private HotelServiceImpl hotelService;

    private Hotel hotel;

    @BeforeEach
    void setUp() {
        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Grand Hotel");
        hotel.setCity("Amman");
        hotel.setCountry("Jordan");
        hotel.setDescription("A luxury hotel");
        hotel.setRating(4.5);
        hotel.setAddress("123 Main St");
        hotel.setLatitude(31.95);
        hotel.setLongitude(35.93);
        hotel.setAmenities(List.of("FREE_WIFI", "SPA", "SWIMMING_POOL"));
    }

    @Test
    @DisplayName("getHotelById - success")
    void getHotelById_shouldReturnHotel() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        Hotel result = hotelService.getHotelById(1L);

        assertNotNull(result);
        assertEquals("Grand Hotel", result.getName());
        assertEquals("Jordan", result.getCountry());
        verify(hotelRepository).findById(1L);
    }

    @Test
    @DisplayName("getHotelById - not found")
    void getHotelById_shouldThrowWhenNotFound() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(HotelNotFoundException.class,
                () -> hotelService.getHotelById(99L));
    }

    @Test
    @DisplayName("findAll - with filters returns paged results")
    @SuppressWarnings("unchecked")
    void findAll_shouldReturnFilteredResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Hotel> page = new PageImpl<>(List.of(hotel), pageable, 1);

        when(hotelRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        HotelPagedResponse<Hotel> result = hotelService.findAll(
                "Jordan", "Amman", 4.0, 200.0,
                List.of("FREE_WIFI"), pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Grand Hotel", result.getContent().get(0).getName());
    }

    @Test
    @DisplayName("findAll - with null filters returns all")
    @SuppressWarnings("unchecked")
    void findAll_shouldReturnAllWhenNoFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Hotel hotel2 = new Hotel();
        hotel2.setId(2L);
        hotel2.setName("Beach Hotel");
        Page<Hotel> page = new PageImpl<>(Arrays.asList(hotel, hotel2), pageable, 2);

        when(hotelRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(page);

        HotelPagedResponse<Hotel> result = hotelService.findAll(
                null, null, null, null, null, pageable);

        assertEquals(2, result.getContent().size());
    }

    @Test
    @DisplayName("updateHotel - success")
    void updateHotel_shouldUpdateFields() {
        Hotel updated = new Hotel();
        updated.setName("Updated Name");
        updated.setCity("Aqaba");
        updated.setCountry("Jordan");
        updated.setDescription("Updated desc");
        updated.setRating(4.8);
        updated.setAddress("456 Sea Rd");
        updated.setLatitude(29.53);
        updated.setLongitude(35.00);
        updated.setAmenities(List.of("GYM", "BAR"));

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        when(hotelRepository.save(any(Hotel.class))).thenAnswer(i -> i.getArgument(0));

        Hotel result = hotelService.updateHotel(1L, updated);

        assertEquals("Updated Name", result.getName());
        assertEquals("Aqaba", result.getCity());
        assertEquals("456 Sea Rd", result.getAddress());
        assertEquals(List.of("GYM", "BAR"), result.getAmenities());
    }

    @Test
    @DisplayName("updateHotel - not found")
    void updateHotel_shouldThrowWhenNotFound() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(HotelNotFoundException.class,
                () -> hotelService.updateHotel(99L, hotel));
    }

    @Test
    @DisplayName("deleteHotel - success")
    void deleteHotel_shouldDelete() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));
        doNothing().when(hotelRepository).delete(hotel);

        hotelService.deleteHotel(1L);

        verify(hotelRepository).delete(hotel);
    }

    @Test
    @DisplayName("deleteHotel - not found")
    void deleteHotel_shouldThrowWhenNotFound() {
        when(hotelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(HotelNotFoundException.class,
                () -> hotelService.deleteHotel(99L));
    }
}
