package org.project.projectstep1zanix.catalog.review;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
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
import org.project.projectstep1zanix.Users.Guest;
import org.project.projectstep1zanix.Users.GuestRepository;
import org.project.projectstep1zanix.booking.Booking;
import org.project.projectstep1zanix.booking.BookingRepository;
import org.project.projectstep1zanix.booking.BookingStatus;
import org.project.projectstep1zanix.catalog.Hotel.HotelNotFoundException;
import org.project.projectstep1zanix.catalog.Hotel.HotelRepository;
import org.project.projectstep1zanix.common.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private HotelRepository hotelRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private GuestRepository guestRepository;
    @Mock private AppUserRepository appUserRepository;
    @Mock private ReviewMapper reviewMapper;
    @Mock private Authentication authentication;
    @Mock private SecurityContext securityContext;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review review;
    private ReviewRequestDto requestDto;
    private ReviewResponseDto responseDto;
    private Guest guest;
    private AppUser appUser;

    @BeforeEach
    void setUp() {
        appUser = new AppUser();
        appUser.setId(10L);
        appUser.setUsername("testguest");
        appUser.setEmail("test@test.com");

        guest = new Guest();
        guest.setId(5L);
        guest.setUser(appUser);

        review = new Review();
        review.setId(1L);
        review.setHotelId(1L);
        review.setGuestId(5L);
        review.setRating(4);
        review.setComment("Great stay!");
        review.setCreatedAt(LocalDateTime.now());

        requestDto = new ReviewRequestDto();
        requestDto.setRating(4);
        requestDto.setComment("Great stay!");

        responseDto = new ReviewResponseDto();
        responseDto.setId(1L);
        responseDto.setHotelId(1L);
        responseDto.setGuestId(5L);
        responseDto.setRating(4);
        responseDto.setComment("Great stay!");
    }

    private void mockSecurityContext() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("testguest");
        when(appUserRepository.findByEmail("testguest")).thenReturn(Optional.empty());
        when(appUserRepository.findByUsername("testguest")).thenReturn(Optional.of(appUser));
        when(guestRepository.findByUserId(10L)).thenReturn(Optional.of(guest));
    }

    @Test
    @DisplayName("createReview - success with confirmed booking")
    void createReview_shouldSucceed() {
        mockSecurityContext();

        Booking booking = new Booking();
        booking.setHotelId(1L);
        booking.setStatus(BookingStatus.CONFIRMED);

        when(hotelRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.existsByHotelIdAndGuestId(1L, 5L)).thenReturn(false);
        when(bookingRepository.findByGuest_Id(5L)).thenReturn(List.of(booking));
        when(reviewMapper.toEntity(requestDto, 1L, 5L)).thenReturn(review);
        when(reviewRepository.save(review)).thenReturn(review);
        when(reviewMapper.toDto(review)).thenReturn(responseDto);

        ReviewResponseDto result = reviewService.createReview(1L, requestDto);

        assertNotNull(result);
        assertEquals(4, result.getRating());
        verify(reviewRepository).save(review);
    }

    @Test
    @DisplayName("createReview - hotel not found")
    void createReview_shouldThrowWhenHotelNotFound() {
        when(hotelRepository.existsById(99L)).thenReturn(false);

        assertThrows(HotelNotFoundException.class,
                () -> reviewService.createReview(99L, requestDto));
    }

    @Test
    @DisplayName("createReview - no confirmed booking")
    void createReview_shouldThrowWhenNoConfirmedBooking() {
        mockSecurityContext();

        Booking pending = new Booking();
        pending.setHotelId(1L);
        pending.setStatus(BookingStatus.PENDING);

        when(hotelRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.existsByHotelIdAndGuestId(1L, 5L)).thenReturn(false);
        when(bookingRepository.findByGuest_Id(5L)).thenReturn(List.of(pending));

        assertThrows(IllegalStateException.class,
                () -> reviewService.createReview(1L, requestDto));
    }

    @Test
    @DisplayName("createReview - already reviewed")
    void createReview_shouldThrowWhenAlreadyReviewed() {
        mockSecurityContext();
        when(hotelRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.existsByHotelIdAndGuestId(1L, 5L)).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> reviewService.createReview(1L, requestDto));
    }

    @Test
    @DisplayName("getReviewsByHotelId - returns list")
    void getReviewsByHotelId_shouldReturn() {
        when(reviewRepository.findByHotelId(1L)).thenReturn(List.of(review));
        when(reviewMapper.toDto(review)).thenReturn(responseDto);

        List<ReviewResponseDto> result = reviewService.getReviewsByHotelId(1L);

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("getReviewSummary - returns averages")
    void getReviewSummary_shouldReturnAverageAndCount() {
        when(reviewRepository.findAverageRatingByHotelId(1L)).thenReturn(4.25);
        when(reviewRepository.countByHotelId(1L)).thenReturn(10L);

        ReviewSummaryDto result = reviewService.getReviewSummary(1L);

        assertEquals(4.25, result.getAverageRating());
        assertEquals(10L, result.getTotalReviews());
    }

    @Test
    @DisplayName("getReviewSummary - no reviews")
    void getReviewSummary_shouldReturnZeroWhenNoReviews() {
        when(reviewRepository.findAverageRatingByHotelId(1L)).thenReturn(null);
        when(reviewRepository.countByHotelId(1L)).thenReturn(0L);

        ReviewSummaryDto result = reviewService.getReviewSummary(1L);

        assertEquals(0.0, result.getAverageRating());
        assertEquals(0L, result.getTotalReviews());
    }

    @Test
    @DisplayName("deleteReview - not found")
    void deleteReview_shouldThrowWhenNotFound() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> reviewService.deleteReview(1L, 99L));
    }
}
