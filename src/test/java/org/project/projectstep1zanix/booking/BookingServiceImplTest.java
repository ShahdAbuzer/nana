package org.project.projectstep1zanix.booking;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.project.projectstep1zanix.Payment.PaymentService;
import org.project.projectstep1zanix.Users.AppUser;
import org.project.projectstep1zanix.Users.AppUserRepository;
import org.project.projectstep1zanix.Users.Guest;
import org.project.projectstep1zanix.Users.GuestRepository;
import org.project.projectstep1zanix.availability_pricing.Availability.AvailabilityConflictException;
import org.project.projectstep1zanix.availability_pricing.Availability.AvailabilityResponseDto;
import org.project.projectstep1zanix.availability_pricing.Availability.AvailabilityService;
import org.project.projectstep1zanix.catalog.Hotel.HotelRepository;
import org.project.projectstep1zanix.catalog.RoomType.RoomType;
import org.project.projectstep1zanix.catalog.RoomType.RoomTypeRepository;
import org.project.projectstep1zanix.notification.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private BookingMapper bookingMapper;
    @Mock private AvailabilityService availabilityService;
    @Mock private NotificationService notificationService;
    @Mock private HotelRepository hotelRepository;
    @Mock private PaymentService paymentService;
    @Mock private RoomTypeRepository roomTypeRepository;
    @Mock private GuestRepository guestRepository;
    @Mock private AppUserRepository appUserRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingRequestDto request;
    private RoomType roomType;
    private AppUser user;
    private Guest guest;

    @BeforeEach
    void setup() {
        request = new BookingRequestDto();
        request.setHotelId(1L);
        request.setRoomTypeId(1L);
        request.setCheckInDate(LocalDate.now().plusDays(1));
        request.setCheckOutDate(LocalDate.now().plusDays(3));
        request.setNumberOfGuests(2);

        roomType = new RoomType();
        roomType.setCapacity(3);
        roomType.setTotalRooms(5);
        roomType.setBasePrice(100.0);

        user = new AppUser();
        user.setId(1L);
        user.setEmail("test@test.com");

        guest = new Guest();
        guest.setUser(user);
    }

    // SUCCESS CASE
    @Test
    void shouldCreateBookingSuccessfully() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("test@test.com");
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(roomType));

        AvailabilityResponseDto availability = new AvailabilityResponseDto(true, 5,
                request.getCheckInDate(), request.getCheckOutDate());
        when(availabilityService.checkAvailability(any(), isNull())).thenReturn(availability);

        when(bookingRepository.findOverlappingBookings(any(), any(), any()))
                .thenReturn(List.of());

        when(appUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(guestRepository.findByUserId(user.getId())).thenReturn(Optional.of(guest));

        Booking booking = new Booking();
        when(bookingMapper.toEntity(request)).thenReturn(booking);
        when(bookingRepository.save(any())).thenReturn(booking);
        when(bookingMapper.toDto(any())).thenReturn(new BookingResponseDto());

        BookingResponseDto result = bookingService.createBooking(request);

        assertNotNull(result);
        verify(bookingRepository, times(1)).save(any());
    }

    // INVALID DATE
    @Test
    void shouldThrowException_whenInvalidDates() {
        request.setCheckOutDate(request.getCheckInDate());

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(request));
    }

    // CAPACITY EXCEEDED
    @Test
    void shouldThrowException_whenGuestsExceedCapacity() {
        request.setNumberOfGuests(10);
        when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(roomType));

        assertThrows(IllegalArgumentException.class,
                () -> bookingService.createBooking(request));
    }

    // ROOM NOT AVAILABLE
    @Test
    void shouldThrowException_whenNotAvailable() {
        when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(roomType));

        AvailabilityResponseDto availability =
                new AvailabilityResponseDto(false, 0,
                        request.getCheckInDate(), request.getCheckOutDate());

        when(availabilityService.checkAvailability(any(), isNull()))
                .thenReturn(availability);

        assertThrows(AvailabilityConflictException.class,
                () -> bookingService.createBooking(request));
    }

    // DOUBLE BOOKING
    @Test
    void shouldThrowException_whenOverlappingBookingsExceeded() {
        when(roomTypeRepository.findById(1L)).thenReturn(Optional.of(roomType));

        AvailabilityResponseDto availability =
                new AvailabilityResponseDto(true, 5,
                        request.getCheckInDate(), request.getCheckOutDate());

        when(availabilityService.checkAvailability(any(), isNull()))
                .thenReturn(availability);

        when(bookingRepository.findOverlappingBookings(any(), any(), any()))
                .thenReturn(List.of(new Booking(), new Booking(), new Booking(), new Booking(), new Booking()));

        assertThrows(AvailabilityConflictException.class,
                () -> bookingService.createBooking(request));
    }
}
