package org.project.projectstep1zanix.availability_pricing.availability;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.projectstep1zanix.availability_pricing.Availability.Availability;
import org.project.projectstep1zanix.availability_pricing.Availability.AvailabilityConflictException;
import org.project.projectstep1zanix.availability_pricing.Availability.AvailabilityNotFoundException;
import org.project.projectstep1zanix.availability_pricing.Availability.AvailabilityRepository;
import org.project.projectstep1zanix.availability_pricing.Availability.AvailabilityServiceImpl;
import org.project.projectstep1zanix.availability_pricing.Availability.InvalidDateRangeException;
import org.project.projectstep1zanix.catalog.RoomType.RoomType;
import org.project.projectstep1zanix.catalog.RoomType.RoomTypeRepository;


@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    @Mock private AvailabilityRepository availabilityRepository;
    @Mock private RoomTypeRepository roomTypeRepository;

    @InjectMocks
    private AvailabilityServiceImpl availabilityService;

    private Availability availability;
    private RoomType roomType;

    @BeforeEach
    void setup() {
        availability = new Availability();
        availability.setHotelId(1L);
        availability.setRoomTypeId(1L);
        availability.setRoomsReserved(1);
        availability.setStartDate(LocalDate.now().plusDays(1));
        availability.setEndDate(LocalDate.now().plusDays(3));

        roomType = new RoomType();
        roomType.setTotalRooms(5);
        roomType.setCapacity(3);
    }

    //  RESERVE SUCCESS
    @Test
    void shouldReserveSuccessfully() {
        when(roomTypeRepository.findByIdAndHotelId(1L, 1L))
                .thenReturn(Optional.of(roomType));

        when(availabilityRepository.existsByHotelIdAndRoomTypeIdAndBookingIdAndStartDateAndEndDate(
                any(), any(), any(), any(), any())).thenReturn(false);

        when(availabilityRepository.sumReservedRooms(any(), any(), any(), any(), any(), any()))
                .thenReturn(1);

        when(availabilityRepository.save(any())).thenReturn(availability);

        Availability result = availabilityService.reserve(availability);

        assertNotNull(result);
    }

    //  INVALID DATE
    @Test
    void shouldThrowException_whenInvalidDate() {
        availability.setEndDate(availability.getStartDate());

        assertThrows(InvalidDateRangeException.class,
                () -> availabilityService.reserve(availability));
    }

    //  DUPLICATE AVAILABILITY
    @Test
    void shouldThrowException_whenAlreadyExists() {
        when(availabilityRepository.existsByHotelIdAndRoomTypeIdAndBookingIdAndStartDateAndEndDate(
                any(), any(), any(), any(), any())).thenReturn(true);

        assertThrows(AvailabilityConflictException.class,
                () -> availabilityService.reserve(availability));
    }

    //   NOT ENOUGH ROOMS
    @Test
    void shouldThrowException_whenNoRoomsAvailable() {
        when(roomTypeRepository.findByIdAndHotelId(1L, 1L))
                .thenReturn(Optional.of(roomType));

        when(availabilityRepository.existsByHotelIdAndRoomTypeIdAndBookingIdAndStartDateAndEndDate(
                any(), any(), any(), any(), any())).thenReturn(false);

        when(availabilityRepository.sumReservedRooms(any(), any(), any(), any(), any(), any()))
                .thenReturn(5); // full

        assertThrows(AvailabilityConflictException.class,
                () -> availabilityService.reserve(availability));
    }

    // NOT FOUND
    @Test
    void shouldThrowException_whenNotFound() {
        when(availabilityRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AvailabilityNotFoundException.class,
                () -> availabilityService.findById(1L));
    }
}
