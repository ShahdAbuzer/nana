package org.project.projectstep1zanix.Security;

import org.project.projectstep1zanix.Payment.Payment;
import org.project.projectstep1zanix.Payment.PaymentService;
import org.project.projectstep1zanix.Users.Guest;
import org.project.projectstep1zanix.Users.GuestRepository;
import org.project.projectstep1zanix.availability_pricing.Availability.Availability;
import org.project.projectstep1zanix.availability_pricing.Availability.AvailabilityRequestDto;
import org.project.projectstep1zanix.availability_pricing.Availability.AvailabilityService;
import org.project.projectstep1zanix.availability_pricing.Pricing.PricingRule;
import org.project.projectstep1zanix.availability_pricing.Pricing.PricingRuleRequestDto;
import org.project.projectstep1zanix.availability_pricing.Pricing.PricingService;
import org.project.projectstep1zanix.booking.Booking;
import org.project.projectstep1zanix.booking.BookingService;
import org.project.projectstep1zanix.catalog.Hotel.Hotel;
import org.project.projectstep1zanix.catalog.Hotel.HotelService;
import org.project.projectstep1zanix.catalog.RoomType.RoomType;
import org.project.projectstep1zanix.catalog.RoomType.RoomTypeRequestDto;
import org.project.projectstep1zanix.catalog.RoomType.RoomTypeService;
import org.project.projectstep1zanix.chapter.Chapter;
import org.project.projectstep1zanix.chapter.ChapterNotFoundException;
import org.project.projectstep1zanix.chapter.ChapterRepository;
import org.project.projectstep1zanix.chapter.ChapterStory;
import org.project.projectstep1zanix.chapter.ChapterStoryRepository;
import org.project.projectstep1zanix.chapter.ChapterVisibility;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("authz")
public class AuthorizationService {

    private final HotelService hotelService;
    private final RoomTypeService roomTypeService;
    private final AvailabilityService availabilityService;
    private final PricingService pricingService;
    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final GuestRepository guestRepository;
    private final ChapterRepository chapterRepository;
    private final ChapterStoryRepository chapterStoryRepository;

    public AuthorizationService(
            HotelService hotelService,
            RoomTypeService roomTypeService,
            AvailabilityService availabilityService,
            PricingService pricingService,
            BookingService bookingService,
            PaymentService paymentService,
            GuestRepository guestRepository,
            ChapterRepository chapterRepository,
            ChapterStoryRepository chapterStoryRepository
    ) {
        this.hotelService = hotelService;
        this.roomTypeService = roomTypeService;
        this.availabilityService = availabilityService;
        this.pricingService = pricingService;
        this.bookingService = bookingService;
        this.paymentService = paymentService;
        this.guestRepository = guestRepository;
        this.chapterRepository = chapterRepository;
        this.chapterStoryRepository = chapterStoryRepository;
    }

    private Jwt currentJwt(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }

        return jwt;
    }

    private Long currentUserId(Authentication authentication) {
        Jwt jwt = currentJwt(authentication);

        if (jwt == null) {
            return null;
        }

        Number claimUserId = jwt.getClaim("userId");
        return claimUserId != null ? claimUserId.longValue() : null;
    }

    private String currentUsername(Authentication authentication) {
        return authentication != null ? authentication.getName() : null;
    }

    public boolean isSelf(Long userId, Authentication authentication) {
        Long currentUserId = currentUserId(authentication);
        return currentUserId != null && userId != null && currentUserId.equals(userId);
    }

    public boolean isGuestOwner(Long guestId, Authentication authentication) {
        if (guestId == null || authentication == null) {
            return false;
        }

        Long currentUserId = currentUserId(authentication);

        if (currentUserId == null) {
            return false;
        }

        return guestRepository.findById(guestId)
                .map(Guest::getUser)
                .map(user -> user.getId().equals(currentUserId))
                .orElse(false);
    }

    public boolean isManagerOfHotel(Authentication authentication, Long hotelId) {
        if (authentication == null || hotelId == null) {
            return false;
        }

        Hotel hotel = hotelService.getHotelById(hotelId);
        Long currentUserId = currentUserId(authentication);

        return currentUserId != null
                && hotel.getManager() != null
                && hotel.getManager().getUser() != null
                && hotel.getManager().getUser().getId() != null
                && hotel.getManager().getUser().getId().equals(currentUserId);
    }

    public boolean canCreateRoomType(Authentication authentication, RoomTypeRequestDto requestDto) {
        return authentication != null
                && requestDto != null
                && isManagerOfHotel(authentication, requestDto.getHotelId());
    }

    public boolean canManageRoomType(Authentication authentication, Long roomTypeId) {
        if (authentication == null || roomTypeId == null) {
            return false;
        }

        RoomType roomType = roomTypeService.getRoomTypeEntityById(roomTypeId);

        return roomType.getHotelId() != null
                && isManagerOfHotel(authentication, roomType.getHotelId());
    }

    public boolean canCreateAvailability(Authentication authentication, AvailabilityRequestDto request) {
        return authentication != null
                && request != null
                && isManagerOfHotel(authentication, request.getHotelId());
    }

    public boolean canManageAvailability(Authentication authentication, Long availabilityId) {
        if (authentication == null || availabilityId == null) {
            return false;
        }

        Availability availability = availabilityService.findById(availabilityId);

        return availability.getHotelId() != null
                && isManagerOfHotel(authentication, availability.getHotelId());
    }

    public boolean canCreatePricingRule(Authentication authentication, PricingRuleRequestDto request) {
        return authentication != null
                && request != null
                && isManagerOfHotel(authentication, request.getHotelId());
    }

    public boolean canManagePricingRule(Authentication authentication, Long ruleId) {
        if (authentication == null || ruleId == null) {
            return false;
        }

        PricingRule rule = pricingService.getPricingRuleEntityById(ruleId);

        return rule.getHotelId() != null
                && isManagerOfHotel(authentication, rule.getHotelId());
    }

    public boolean canCreateBooking(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }

    public boolean canAccessBooking(Authentication authentication, Long bookingId) {
        if (authentication == null || bookingId == null) {
            return false;
        }

        Booking booking = bookingService.getBookingEntityById(bookingId);
        Long currentUserId = currentUserId(authentication);
        String currentUsername = currentUsername(authentication);

        return booking.getGuest() != null
                && booking.getGuest().getUser() != null
                && (
                (currentUserId != null && booking.getGuest().getUser().getId().equals(currentUserId))
                        || (currentUsername != null && currentUsername.equals(booking.getGuest().getUser().getUsername()))
        );
    }

    public boolean canManageBooking(Authentication authentication, Long bookingId) {
        if (authentication == null || bookingId == null) {
            return false;
        }

        Booking booking = bookingService.getBookingEntityById(bookingId);

        return booking.getHotelId() != null
                && isManagerOfHotel(authentication, booking.getHotelId());
    }

    public boolean canAccessGuestBookings(Authentication authentication, Long guestId) {
        return isGuestOwner(guestId, authentication);
    }

    public boolean canAccessPayment(Authentication authentication, Long paymentId) {
        if (authentication == null || paymentId == null) {
            return false;
        }

        Payment payment = paymentService.getPaymentEntityById(paymentId);
        return canAccessBooking(authentication, payment.getBookingId());
    }

    public boolean canManagePayment(Authentication authentication, Long paymentId) {
        if (authentication == null || paymentId == null) {
            return false;
        }

        Payment payment = paymentService.getPaymentEntityById(paymentId);
        return canManageBooking(authentication, payment.getBookingId());
    }

    public boolean canAccessPaymentByBooking(Authentication authentication, Long bookingId) {
        return canAccessBooking(authentication, bookingId);
    }

    public boolean canManagePaymentByBooking(Authentication authentication, Long bookingId) {
        return canManageBooking(authentication, bookingId);
    }

    public boolean canManageChapter(Authentication authentication, Long chapterId) {
        if (authentication == null || chapterId == null) {
            return false;
        }

        if (hasRole(authentication, "ADMIN")) {
            return true;
        }

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ChapterNotFoundException(chapterId));

        Long currentGuestId = currentGuestId(authentication);

        return currentGuestId != null
                && chapter.getGuestId() != null
                && chapter.getGuestId().equals(currentGuestId);
    }

    public boolean canReadChapter(Authentication authentication, Long chapterId) {
        if (chapterId == null) {
            return false;
        }

        Chapter chapter = chapterRepository.findById(chapterId)
                .orElseThrow(() -> new ChapterNotFoundException(chapterId));

        return chapter.getVisibility() == ChapterVisibility.PUBLIC
                || canManageChapter(authentication, chapterId);
    }

    public boolean canManageTemporaryStory(Authentication authentication, Long storyId) {
        if (authentication == null || storyId == null) {
            return false;
        }

        if (hasRole(authentication, "ADMIN")) {
            return true;
        }

        ChapterStory story = chapterStoryRepository.findById(storyId)
                .orElse(null);

        if (story == null) {
            return false;
        }

        Long currentGuestId = currentGuestId(authentication);

        return currentGuestId != null
                && story.getGuestId() != null
                && story.getGuestId().equals(currentGuestId);
    }

    private Long currentGuestId(Authentication authentication) {
        Long userId = currentUserId(authentication);

        if (userId != null) {
            return guestRepository.findByUserId(userId)
                    .map(Guest::getId)
                    .orElse(null);
        }

        String login = currentUsername(authentication);

        if (login == null || login.isBlank() || "anonymousUser".equals(login)) {
            return null;
        }

        return guestRepository.findByUserUsername(login)
                .or(() -> guestRepository.findByUserEmail(login))
                .map(Guest::getId)
                .orElse(null);
    }

    private boolean hasRole(Authentication authentication, String role) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }

        String expectedAuthority = "ROLE_" + role;

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> expectedAuthority.equals(authority.getAuthority()));
    }
}