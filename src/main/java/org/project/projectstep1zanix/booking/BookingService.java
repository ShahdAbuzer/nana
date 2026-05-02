package org.project.projectstep1zanix.booking;

import org.project.projectstep1zanix.common.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto requestDto);

    BookingResponseDto confirmBooking(Long id);

    BookingResponseDto cancelBooking(Long id);

    BookingResponseDto getBookingById(Long id);

    PagedResponse<BookingResponseDto> getBookingHistory(Long guestId, Pageable pageable);

    PagedResponse<BookingResponseDto> getUpcomingBookings(Long guestId, Pageable pageable);

    Booking getBookingEntityById(Long id);
}