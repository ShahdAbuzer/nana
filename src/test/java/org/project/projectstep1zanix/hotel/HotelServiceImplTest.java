package org.project.projectstep1zanix.hotel;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.projectstep1zanix.Users.AppUser;
import org.project.projectstep1zanix.Users.AppUserRepository;
import org.project.projectstep1zanix.Users.Manager;
import org.project.projectstep1zanix.Users.ManagerRepository;
import org.project.projectstep1zanix.catalog.Hotel.Hotel;
import org.project.projectstep1zanix.catalog.Hotel.HotelNotFoundException;
import org.project.projectstep1zanix.catalog.Hotel.HotelPagedResponse;
import org.project.projectstep1zanix.catalog.Hotel.HotelRepository;
import org.project.projectstep1zanix.catalog.Hotel.HotelServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@DisplayName("HotelServiceImpl Unit Tests")
class HotelServiceImplTest {

    @Mock private HotelRepository hotelRepository;
    @Mock private AppUserRepository appUserRepository;
    @Mock private ManagerRepository managerRepository;

    private HotelServiceImpl hotelService;

    @BeforeEach
    void setup() {
        hotelService = new HotelServiceImpl(
                hotelRepository,
                appUserRepository,
                managerRepository
        );
    }

    private Hotel buildHotel(Long id, String name, String city, String country, Double rating) {
        Hotel hotel = new Hotel();
        hotel.setId(id);
        hotel.setName(name);
        hotel.setCity(city);
        hotel.setCountry(country);
        hotel.setDescription("desc");
        hotel.setRating(rating);
        return hotel;
    }

    // createHotel

@Test
void createHotel_success() {

    Authentication auth = mock(Authentication.class);
    when(auth.getName()).thenReturn("test@test.com");
    SecurityContextHolder.getContext().setAuthentication(auth);

    AppUser user = new AppUser();
    user.setId(1L);
    user.setEmail("test@test.com");

    when(appUserRepository.findByEmail(any()))
            .thenReturn(Optional.of(user));

    Manager manager = new Manager();
    manager.setUser(user);

    when(managerRepository.findByUserId(any()))
            .thenReturn(Optional.of(manager));

    Hotel input = buildHotel(null, "Hotel", "Paris", "France", 4.5);
    Hotel saved = buildHotel(1L, "Hotel", "Paris", "France", 4.5);

    when(hotelRepository.save(input)).thenReturn(saved);

    Hotel result = hotelService.createHotel(input);

    assertThat(result.getId()).isEqualTo(1L);
}

    // getHotelById
    @Test
    void getHotel_success() {
        Hotel hotel = buildHotel(1L, "Hotel", "Paris", "France", 4.5);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        Hotel result = hotelService.getHotelById(1L);

        assertThat(result).isNotNull();
    }

    @Test
    void getHotel_notFound() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelService.getHotelById(1L))
                .isInstanceOf(HotelNotFoundException.class);
    }

    
    // updateHotel
    @Test
    void updateHotel_success() {
        Hotel existing = buildHotel(1L, "Old", "Paris", "France", 3.0);
        Hotel updates = buildHotel(null, "New", "Lyon", "France", 4.8);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(hotelRepository.save(existing)).thenReturn(existing);

        Hotel result = hotelService.updateHotel(1L, updates);

        assertThat(result.getName()).isEqualTo("New");
        verify(hotelRepository).save(existing);
    }

    @Test
    void updateHotel_notFound() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelService.updateHotel(1L, new Hotel()))
                .isInstanceOf(HotelNotFoundException.class);
    }

    // deleteHotel
    @Test
    void deleteHotel_success() {
        Hotel hotel = buildHotel(1L, "Hotel", "Paris", "France", 4.5);

        when(hotelRepository.findById(1L)).thenReturn(Optional.of(hotel));

        hotelService.deleteHotel(1L);

        verify(hotelRepository).delete(hotel);
    }

    @Test
    void deleteHotel_notFound() {
        when(hotelRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> hotelService.deleteHotel(1L))
                .isInstanceOf(HotelNotFoundException.class);
    }

    // findAll
    @Test
    void findAll_success() {
        Pageable pageable = PageRequest.of(0, 10);

        Hotel h1 = buildHotel(1L, "A", "Paris", "France", 4.0);
        Hotel h2 = buildHotel(2L, "B", "Rome", "Italy", 4.5);

        Page<Hotel> page = new PageImpl<>(List.of(h1, h2), pageable, 2);

        when(hotelRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), eq(pageable)))
        .thenReturn(page);

        HotelPagedResponse<Hotel> result =
                hotelService.findAll(null, null, null, null, null, pageable);

        assertThat(result.getContent()).hasSize(2);
    }
}