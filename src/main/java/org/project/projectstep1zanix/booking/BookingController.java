package org.project.projectstep1zanix.booking;

import org.project.projectstep1zanix.common.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }
@Operation(summary = "Create booking", description = "Creates a booking after validating room capacity and availability.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Booking created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid booking data"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Room type not found"),
        @ApiResponse(responseCode = "409", description = "Room is not available for the requested dates")
})
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @authz.canCreateBooking(authentication)")
    public ResponseEntity<BookingResponseDto> createBooking(@Valid @RequestBody BookingRequestDto requestDto) {
        return new ResponseEntity<>(bookingService.createBooking(requestDto), HttpStatus.CREATED);
    }
@Operation(summary = "Create booking", description = "Creates a booking after validating room capacity and availability.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Booking created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid booking data"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Room type not found"),
        @ApiResponse(responseCode = "409", description = "Room is not available for the requested dates")
})
    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManageBooking(authentication, #id) or @authz.canAccessBooking(authentication, #id)")
    public ResponseEntity<BookingResponseDto> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }
@Operation(summary = "Get booking history", description = "Returns historical bookings for a guest.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking history retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
})

   @GetMapping("/history")
@PreAuthorize("hasRole('ADMIN') or @authz.canAccessGuestBookings(authentication, #guestId)")
public ResponseEntity<PagedResponse<BookingResponseDto>> getBookingHistory(
        @RequestParam Long guestId,
        Pageable pageable
) {
    return ResponseEntity.ok(bookingService.getBookingHistory(guestId, pageable));
}
@Operation(summary = "Get upcoming bookings", description = "Returns upcoming bookings for a guest.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Upcoming bookings retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
})
   @GetMapping("/upcoming")
@PreAuthorize("hasRole('ADMIN') or @authz.canAccessGuestBookings(authentication, #guestId)")
public ResponseEntity<PagedResponse<BookingResponseDto>> getUpcomingBookings(
        @RequestParam Long guestId,
        Pageable pageable
) {
    return ResponseEntity.ok(bookingService.getUpcomingBookings(guestId, pageable));
}
@Operation(summary = "Get booking by ID", description = "Retrieves booking details by its ID.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Booking retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Booking not found")
})
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManageBooking(authentication, #id) or @authz.canAccessBooking(authentication, #id)")
    public ResponseEntity<BookingResponseDto> getBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }
}