package org.project.projectstep1zanix.booking;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.project.projectstep1zanix.Payment.PaymentService;
import org.project.projectstep1zanix.Users.AppUser;
import org.project.projectstep1zanix.Users.AppUserRepository;
import org.project.projectstep1zanix.Users.Guest;
import org.project.projectstep1zanix.Users.GuestNotFoundException;
import org.project.projectstep1zanix.Users.GuestRepository;
import org.project.projectstep1zanix.availability_pricing.Availability.AvailabilityConflictException;
import org.project.projectstep1zanix.availability_pricing.Availability.AvailabilityRequestDto;
import org.project.projectstep1zanix.availability_pricing.Availability.AvailabilityResponseDto;
import org.project.projectstep1zanix.availability_pricing.Availability.AvailabilityService;
import org.project.projectstep1zanix.catalog.Hotel.Hotel;
import org.project.projectstep1zanix.catalog.Hotel.HotelNotFoundException;
import org.project.projectstep1zanix.catalog.Hotel.HotelRepository;
import org.project.projectstep1zanix.catalog.RoomType.RoomType;
import org.project.projectstep1zanix.catalog.RoomType.RoomTypeNotFoundException;
import org.project.projectstep1zanix.catalog.RoomType.RoomTypeRepository;
import org.project.projectstep1zanix.common.PagedResponse;
import org.project.projectstep1zanix.notification.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final AvailabilityService availabilityPricingService;
    private final NotificationService notificationService;
    private final HotelRepository hotelRepository;
    private final PaymentService paymentService;
    private final RoomTypeRepository roomTypeRepository;
    private final GuestRepository guestRepository;
    private final AppUserRepository appUserRepository;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            BookingMapper bookingMapper,
            AvailabilityService availabilityPricingService,
            NotificationService notificationService,
            HotelRepository hotelRepository,
            PaymentService paymentService,
            RoomTypeRepository roomTypeRepository,
            GuestRepository guestRepository,
            AppUserRepository appUserRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.availabilityPricingService = availabilityPricingService;
        this.notificationService = notificationService;
        this.hotelRepository = hotelRepository;
        this.paymentService = paymentService;
        this.roomTypeRepository = roomTypeRepository;
        this.guestRepository = guestRepository;
        this.appUserRepository = appUserRepository;
    }

    @Override
@Transactional
public BookingResponseDto createBooking(BookingRequestDto requestDto) {

    if (requestDto.getCheckInDate() == null || requestDto.getCheckOutDate() == null) {
        throw new IllegalArgumentException("Check-in and check-out dates are required");
    }

    if (!requestDto.getCheckOutDate().isAfter(requestDto.getCheckInDate())) {
        throw new IllegalArgumentException("Check-out date must be after check-in date");
    }

    RoomType roomType = roomTypeRepository.findById(requestDto.getRoomTypeId())
            .orElseThrow(() -> new RoomTypeNotFoundException(requestDto.getRoomTypeId()));

    if (requestDto.getNumberOfGuests() == null || requestDto.getNumberOfGuests() <= 0) {
        throw new IllegalArgumentException("Number of guests must be greater than 0");
    }

    if (requestDto.getNumberOfGuests() > roomType.getCapacity()) {
        throw new IllegalArgumentException(
                "Max allowed guests for this room is " + roomType.getCapacity()
                        + ", but received " + requestDto.getNumberOfGuests()
        );
    }

    AvailabilityRequestDto availabilityRequest = new AvailabilityRequestDto();
    availabilityRequest.setHotelId(requestDto.getHotelId());
    availabilityRequest.setRoomTypeId(requestDto.getRoomTypeId());
    availabilityRequest.setStartDate(requestDto.getCheckInDate());
    availabilityRequest.setEndDate(requestDto.getCheckOutDate());
    availabilityRequest.setRoomsRequested(1);
    availabilityRequest.setGuests(requestDto.getNumberOfGuests());

    AvailabilityResponseDto availabilityResponse =
            availabilityPricingService.checkAvailability(availabilityRequest, null);

    if (!availabilityResponse.isAvailable()) {
        throw new AvailabilityConflictException("Room is not available for the requested dates");
    }

    List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
            requestDto.getRoomTypeId(),
            requestDto.getCheckInDate(),
            requestDto.getCheckOutDate()
    );

    if (overlappingBookings.size() >= roomType.getTotalRooms()) {
        throw new AvailabilityConflictException("No rooms available for selected dates");
    }

    long nights = ChronoUnit.DAYS.between(
            requestDto.getCheckInDate(),
            requestDto.getCheckOutDate()
    );

    double totalPrice = roomType.getBasePrice() * nights;

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String login = authentication.getName();

    AppUser appUser = appUserRepository.findByEmail(login)
            .or(() -> appUserRepository.findByUsername(login))
            .orElseThrow(() -> new RuntimeException("Authenticated user not found"));

    Guest guest = guestRepository.findByUserId(appUser.getId())
            .orElseThrow(() -> new GuestNotFoundException(appUser.getId()));

    Booking booking = bookingMapper.toEntity(requestDto);

    booking.setGuest(guest);
    booking.setStatus(BookingStatus.PENDING);
    booking.setTotalPrice(totalPrice);

    Booking saved = bookingRepository.save(booking);

    return bookingMapper.toDto(saved);
}
    @Override
    @Transactional
    public BookingResponseDto confirmBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Booking is already confirmed");
        }

        booking.setStatus(BookingStatus.CONFIRMED);
        Booking updated = bookingRepository.save(booking);

        String email = booking.getGuest().getUser().getEmail();
        Hotel hotel = hotelRepository.findById(booking.getHotelId())
                .orElseThrow(() -> new HotelNotFoundException(booking.getHotelId()));

        notificationService.sendBookingConfirmation(email, hotel.getName());

        return bookingMapper.toDto(updated);
    }

   @Override
@Transactional
public BookingResponseDto cancelBooking(Long id) {
    Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new BookingNotFoundException(id));

    if (booking.getStatus() == BookingStatus.CANCELLED) {
        throw new IllegalStateException("Booking is already cancelled");
    }

    booking.setStatus(BookingStatus.CANCELLED);
    Booking updated = bookingRepository.save(booking);

    String email = booking.getGuest().getUser().getEmail();

    Hotel hotel = hotelRepository.findById(booking.getHotelId())
            .orElseThrow(() -> new HotelNotFoundException(booking.getHotelId()));

    notificationService.sendBookingCancellation(email, hotel.getName());

    return bookingMapper.toDto(updated);
}

    @Override
    @Transactional(readOnly = true)
    public BookingResponseDto getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<BookingResponseDto> getBookingHistory(Long guestId, Pageable pageable) {
        Specification<Booking> spec = Specification.where(BookingSpecifications.hasGuestId(guestId))
                .and(BookingSpecifications.isHistory(LocalDate.now()));

        Page<Booking> page = bookingRepository.findAll(spec, pageable);
        Page<BookingResponseDto> dtoPage = page.map(bookingMapper::toDto);

        return PagedResponse.from(page, dtoPage.getContent());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<BookingResponseDto> getUpcomingBookings(Long guestId, Pageable pageable) {
        Specification<Booking> spec = Specification.where(BookingSpecifications.hasGuestId(guestId))
                .and(BookingSpecifications.isUpcoming(LocalDate.now()));

        Page<Booking> page = bookingRepository.findAll(spec, pageable);
        Page<BookingResponseDto> dtoPage = page.map(bookingMapper::toDto);

        return PagedResponse.from(page, dtoPage.getContent());
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBookingEntityById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }
}