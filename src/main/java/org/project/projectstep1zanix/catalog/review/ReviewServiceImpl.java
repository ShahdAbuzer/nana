package org.project.projectstep1zanix.catalog.review;

import java.util.List;

import org.project.projectstep1zanix.Users.AppUser;
import org.project.projectstep1zanix.Users.AppUserRepository;
import org.project.projectstep1zanix.Users.Guest;
import org.project.projectstep1zanix.Users.GuestRepository;
import org.project.projectstep1zanix.booking.BookingRepository;
import org.project.projectstep1zanix.booking.BookingStatus;
import org.project.projectstep1zanix.catalog.Hotel.HotelNotFoundException;
import org.project.projectstep1zanix.catalog.Hotel.HotelRepository;
import org.project.projectstep1zanix.common.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;
    private final GuestRepository guestRepository;
    private final AppUserRepository appUserRepository;
    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             HotelRepository hotelRepository,
                             BookingRepository bookingRepository,
                             GuestRepository guestRepository,
                             AppUserRepository appUserRepository,
                             ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.hotelRepository = hotelRepository;
        this.bookingRepository = bookingRepository;
        this.guestRepository = guestRepository;
        this.appUserRepository = appUserRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    @Transactional
    public ReviewResponseDto createReview(Long hotelId, ReviewRequestDto dto) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new HotelNotFoundException(hotelId);
        }

        // Resolve current guest
        Guest guest = resolveCurrentGuest();

        // Check if guest already reviewed this hotel
        if (reviewRepository.existsByHotelIdAndGuestId(hotelId, guest.getId())) {
            throw new IllegalStateException("You have already reviewed this hotel");
        }

        // Validate: guest must have a CONFIRMED booking for this hotel
        boolean hasConfirmedBooking =
        bookingRepository.existsByGuest_IdAndHotelIdAndStatus(
                guest.getId(),
                hotelId,
                BookingStatus.CONFIRMED
        );

if (!hasConfirmedBooking) {
    throw new IllegalStateException(
            "You can only review a hotel where you have a confirmed booking"
    );
}

Review review = reviewMapper.toEntity(dto, hotelId, guest.getId());
Review saved = reviewRepository.save(review);
return reviewMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsByHotelId(Long hotelId) {
        return reviewRepository.findByHotelId(hotelId).stream()
                .map(reviewMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewSummaryDto getReviewSummary(Long hotelId) {
        Double avg = reviewRepository.findAverageRatingByHotelId(hotelId);
        Long count = reviewRepository.countByHotelId(hotelId);
        return new ReviewSummaryDto(
                avg != null ? Math.round(avg * 100.0) / 100.0 : 0.0,
                count
        );
    }

    @Override
    @Transactional
    public void deleteReview(Long hotelId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Review not found with id: " + reviewId));
        if (!review.getHotelId().equals(hotelId)) {
            throw new ResourceNotFoundException(
                    "Review not found for hotel: " + hotelId);
        }

        // Allow deletion by ADMIN or the review owner
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            Guest guest = resolveCurrentGuest();
            if (!review.getGuestId().equals(guest.getId())) {
                throw new IllegalStateException(
                        "You can only delete your own reviews");
            }
        }

        reviewRepository.delete(review);
    }

    private Guest resolveCurrentGuest() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String login = auth.getName();

        AppUser user = appUserRepository.findByEmail(login)
                .or(() -> appUserRepository.findByUsername(login))
                .orElseThrow(() -> new RuntimeException(
                        "Authenticated user not found"));

        return guestRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException(
                        "Guest profile not found for user"));
    }
}
