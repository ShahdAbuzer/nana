package org.project.projectstep1zanix.chapter;

import java.time.LocalDate;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.project.projectstep1zanix.Payment.PaymentPlanResponseDto;
import org.project.projectstep1zanix.Payment.PaymentService;
import org.project.projectstep1zanix.Users.AppUser;
import org.project.projectstep1zanix.Users.AppUserRepository;
import org.project.projectstep1zanix.Users.Guest;
import org.project.projectstep1zanix.Users.GuestRepository;
import org.project.projectstep1zanix.booking.Booking;
import org.project.projectstep1zanix.booking.BookingNotFoundException;
import org.project.projectstep1zanix.booking.BookingRepository;
import org.project.projectstep1zanix.booking.BookingStatus;
import org.project.projectstep1zanix.catalog.Hotel.Hotel;
import org.project.projectstep1zanix.catalog.Hotel.HotelNotFoundException;
import org.project.projectstep1zanix.catalog.Hotel.HotelRepository;
import org.project.projectstep1zanix.catalog.RoomType.RoomType;
import org.project.projectstep1zanix.catalog.RoomType.RoomTypeRepository;
import org.project.projectstep1zanix.common.PagedResponse;
import org.project.projectstep1zanix.file.FileStorageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ChapterServiceImpl implements ChapterService {

    private static final Set<ChapterVisibleSection> DEFAULT_PUBLIC_SECTIONS = EnumSet.of(
            ChapterVisibleSection.COVER_IMAGE,
            ChapterVisibleSection.BASIC_INFO,
            ChapterVisibleSection.HOTEL_SUMMARY,
            ChapterVisibleSection.INNER_IMAGES,
            ChapterVisibleSection.NEARBY_PLACES,
            ChapterVisibleSection.REVIEWS
    );

    private final ChapterRepository chapterRepository;
    private final ChapterImageRepository chapterImageRepository;
    private final ChapterMapper chapterMapper;
    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final BookingRepository bookingRepository;
    private final GuestRepository guestRepository;
    private final AppUserRepository appUserRepository;
    private final PaymentService paymentService;
    private final FileStorageService fileStorageService;

    public ChapterServiceImpl(
            ChapterRepository chapterRepository,
            ChapterImageRepository chapterImageRepository,
            ChapterMapper chapterMapper,
            HotelRepository hotelRepository,
            RoomTypeRepository roomTypeRepository,
            BookingRepository bookingRepository,
            GuestRepository guestRepository,
            AppUserRepository appUserRepository,
            PaymentService paymentService,
            FileStorageService fileStorageService
    ) {
        this.chapterRepository = chapterRepository;
        this.chapterImageRepository = chapterImageRepository;
        this.chapterMapper = chapterMapper;
        this.hotelRepository = hotelRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.bookingRepository = bookingRepository;
        this.guestRepository = guestRepository;
        this.appUserRepository = appUserRepository;
        this.paymentService = paymentService;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public ChapterResponseDto createChapter(ChapterRequestDto request) {
        validateDateRange(request.getStartDate(), request.getEndDate());

        Guest currentGuest = currentGuestOrThrow();

        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new HotelNotFoundException(request.getHotelId()));

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException(request.getBookingId()));

        validateBookingForChapter(booking, hotel.getId(), currentGuest.getId());

        chapterRepository.findByBookingId(booking.getId())
                .ifPresent(existing -> {
                    throw new IllegalStateException("A chapter already exists for booking id: " + booking.getId());
                });

        Chapter chapter = chapterMapper.toEntity(request);
        chapter.setGuestId(currentGuest.getId());

        normalizeVisibleSections(chapter);

        return toFullResponse(chapterRepository.save(chapter));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterResponseDto> getMyChapters() {
        Long guestId = currentGuestIdOrThrow();

        return chapterRepository.findByGuestIdOrderByCreatedAtDesc(guestId)
                .stream()
                .map(this::toFullResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Object getChapterById(Long id) {
        Chapter chapter = getChapterEntityById(id);

        if (isCurrentUserOwnerOrAdmin(chapter)) {
            return toFullResponse(chapter);
        }

        if (chapter.getVisibility() == ChapterVisibility.PUBLIC) {
            return toPublicResponse(chapter);
        }

        throw new AccessDeniedException("Private chapter can be viewed only by owner or admin");
    }

    @Override
    public ChapterResponseDto updateChapter(Long id, ChapterUpdateRequestDto request) {
        Chapter chapter = getChapterEntityById(id);
        ensureCanManage(chapter);

        LocalDate newStartDate = request.getStartDate() != null ? request.getStartDate() : chapter.getStartDate();
        LocalDate newEndDate = request.getEndDate() != null ? request.getEndDate() : chapter.getEndDate();
        validateDateRange(newStartDate, newEndDate);

        Long newHotelId = request.getHotelId() != null ? request.getHotelId() : chapter.getHotelId();
        Long newBookingId = request.getBookingId() != null ? request.getBookingId() : chapter.getBookingId();

        hotelRepository.findById(newHotelId)
                .orElseThrow(() -> new HotelNotFoundException(newHotelId));

        Booking booking = bookingRepository.findById(newBookingId)
                .orElseThrow(() -> new BookingNotFoundException(newBookingId));

        validateBookingForChapter(booking, newHotelId, chapter.getGuestId());

        if (!newBookingId.equals(chapter.getBookingId())) {
            chapterRepository.findByBookingId(newBookingId)
                    .filter(existing -> !existing.getId().equals(chapter.getId()))
                    .ifPresent(existing -> {
                        throw new IllegalStateException("A chapter already exists for booking id: " + newBookingId);
                    });
        }

        if (request.getTitle() != null) {
            chapter.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            chapter.setDescription(request.getDescription());
        }
        if (request.getVisibility() != null) {
            chapter.setVisibility(request.getVisibility());
        }
        if (request.getPublicVisibleSections() != null) {
            chapter.setPublicVisibleSections(new HashSet<>(request.getPublicVisibleSections()));
        }

        chapter.setStartDate(newStartDate);
        chapter.setEndDate(newEndDate);
        chapter.setHotelId(newHotelId);
        chapter.setBookingId(newBookingId);

        normalizeVisibleSections(chapter);

        return toFullResponse(chapterRepository.save(chapter));
    }

    @Override
    public void deleteChapter(Long id) {
        Chapter chapter = getChapterEntityById(id);
        ensureCanManage(chapter);

        if (chapter.getCoverImageUrl() != null) {
            fileStorageService.deleteFile(chapter.getCoverImageUrl());
        }

        List<ChapterImage> images = chapterImageRepository.findByChapterIdOrderBySortOrderAscCreatedAtAsc(id);
        for (ChapterImage image : images) {
            fileStorageService.deleteFile(image.getImageUrl());
        }

        chapterImageRepository.deleteByChapterId(id);
        chapterRepository.delete(chapter);
    }

    @Override
public ChapterResponseDto uploadCoverImage(Long chapterId, MultipartFile image) {
    Chapter chapter = getChapterEntityById(chapterId);
    ensureCanManage(chapter);

    if (chapter.getCoverImageUrl() != null && !chapter.getCoverImageUrl().isEmpty()) {
        throw new IllegalStateException(
                "Chapter already has a cover image. Use the PUT endpoint to replace it."
        );
    }

    String coverImageUrl = fileStorageService.saveFile(image, "chapters/covers");
    chapter.setCoverImageUrl(coverImageUrl);

    Chapter savedChapter = chapterRepository.save(chapter);
    return toFullResponse(savedChapter);
}

@Override
public ChapterResponseDto updateCoverImage(Long chapterId, MultipartFile image) {
    Chapter chapter = getChapterEntityById(chapterId);
    ensureCanManage(chapter);

    String oldCoverImageUrl = chapter.getCoverImageUrl();

    if (oldCoverImageUrl != null && !oldCoverImageUrl.isEmpty()) {
        fileStorageService.deleteFile(oldCoverImageUrl);
    }

    String newCoverImageUrl = fileStorageService.saveFile(image, "chapters/covers");
    chapter.setCoverImageUrl(newCoverImageUrl);

    Chapter savedChapter = chapterRepository.save(chapter);
    return toFullResponse(savedChapter);
}

@Override
public void deleteCoverImage(Long chapterId) {
    Chapter chapter = getChapterEntityById(chapterId);
    ensureCanManage(chapter);

    String coverImageUrl = chapter.getCoverImageUrl();

    if (coverImageUrl == null || coverImageUrl.isEmpty()) {
        throw new IllegalStateException("Chapter with ID " + chapterId + " has no cover image to delete.");
    }

    fileStorageService.deleteFile(coverImageUrl);
    chapter.setCoverImageUrl(null);
    chapterRepository.save(chapter);
}

@Override
public ChapterImageResponseDto addImage(
        Long chapterId,
        MultipartFile image,
        String description,
        Integer sortOrder
) {
    Chapter chapter = getChapterEntityById(chapterId);
    ensureCanManage(chapter);

    String imageUrl = fileStorageService.saveFile(image, "chapters/images");

    ChapterImage chapterImage = new ChapterImage();
    chapterImage.setChapterId(chapter.getId());
    chapterImage.setImageUrl(imageUrl);
    chapterImage.setDescription(description);
    chapterImage.setSortOrder(sortOrder == null ? 0 : sortOrder);

    ChapterImage savedImage = chapterImageRepository.save(chapterImage);
    return chapterMapper.toImageResponseDto(savedImage);
}

@Override
public ChapterImageResponseDto updateImage(
        Long chapterId,
        Long imageId,
        String description,
        Integer sortOrder
) {
    Chapter chapter = getChapterEntityById(chapterId);
    ensureCanManage(chapter);

    ChapterImage image = chapterImageRepository.findByIdAndChapterId(imageId, chapterId)
            .orElseThrow(() -> new ChapterNotFoundException("Chapter image not found with id: " + imageId));

    if (description != null) {
        image.setDescription(description);
    }

    if (sortOrder != null) {
        image.setSortOrder(sortOrder);
    }

    ChapterImage savedImage = chapterImageRepository.save(image);
    return chapterMapper.toImageResponseDto(savedImage);
}

@Override
public void deleteImage(Long chapterId, Long imageId) {
    Chapter chapter = getChapterEntityById(chapterId);
    ensureCanManage(chapter);

    ChapterImage image = chapterImageRepository.findByIdAndChapterId(imageId, chapterId)
            .orElseThrow(() -> new ChapterNotFoundException("Chapter image not found with id: " + imageId));

    if (image.getImageUrl() != null && !image.getImageUrl().isEmpty()) {
        fileStorageService.deleteFile(image.getImageUrl());
    }

    chapterImageRepository.delete(image);
}

    @Override
    public ChapterResponseDto updatePublicVisibleSections(Long chapterId, UpdateChapterVisibleSectionsRequestDto request) {
        Chapter chapter = getChapterEntityById(chapterId);
        ensureCanManage(chapter);

        chapter.setPublicVisibleSections(new HashSet<>(request.getPublicVisibleSections()));
        normalizeVisibleSections(chapter);

        return toFullResponse(chapterRepository.save(chapter));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterVisibleSection> getVisibleSections() {
        return List.of(ChapterVisibleSection.values());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PublicChapterResponseDto> getPublicChapters(
            String country,
            String city,
            Double minRating,
            Pageable pageable
    ) {
        Page<Chapter> page = chapterRepository.findPublicChapters(blankToNull(country), blankToNull(city), minRating, pageable);

        List<PublicChapterResponseDto> content = page.getContent()
                .stream()
                .map(this::toPublicResponse)
                .toList();

        return PagedResponse.from(page, content);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PublicChapterResponseDto> getPublicHotelStories(Long hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new HotelNotFoundException(hotelId);
        }

        return chapterRepository.findByHotelIdAndVisibilityOrderByCreatedAtDesc(hotelId, ChapterVisibility.PUBLIC)
                .stream()
                .map(this::toPublicResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Chapter getChapterEntityById(Long id) {
        return chapterRepository.findById(id)
                .orElseThrow(() -> new ChapterNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCurrentUserOwnerOrAdmin(Chapter chapter) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (isAdmin(authentication)) {
            return true;
        }

        Long guestId = currentGuestIdOrNull();
        return guestId != null && chapter != null && guestId.equals(chapter.getGuestId());
    }

    private ChapterResponseDto toFullResponse(Chapter chapter) {
        ChapterResponseDto dto = chapterMapper.toResponseDto(chapter);

        dto.setImages(
                chapterImageRepository.findByChapterIdOrderBySortOrderAscCreatedAtAsc(chapter.getId())
                        .stream()
                        .map(chapterMapper::toImageResponseDto)
                        .toList()
        );

        dto.setHotel(hotelRepository.findById(chapter.getHotelId()).map(this::toHotelDto).orElse(null));

        Booking booking = bookingRepository.findById(chapter.getBookingId()).orElse(null);

        if (booking != null) {
            dto.setRoomType(roomTypeRepository.findById(booking.getRoomTypeId()).map(this::toRoomTypeDto).orElse(null));
            dto.setPaymentPlan(paymentService.getPaymentPlanByBookingId(booking.getId()));
        }

        return dto;
    }

    private PublicChapterResponseDto toPublicResponse(Chapter chapter) {
        Set<ChapterVisibleSection> sections = visibleSectionsFor(chapter);

        PublicChapterResponseDto dto = new PublicChapterResponseDto();
        dto.setId(chapter.getId());
        dto.setVisibleSections(sections);
        dto.setCreatedAt(chapter.getCreatedAt());

        Optional<Hotel> hotel = hotelRepository.findById(chapter.getHotelId());
        Optional<Booking> booking = bookingRepository.findById(chapter.getBookingId());

        if (sections.contains(ChapterVisibleSection.COVER_IMAGE)) {
            dto.setCoverImageUrl(chapter.getCoverImageUrl());
        }

        if (sections.contains(ChapterVisibleSection.BASIC_INFO)) {
            dto.setTitle(chapter.getTitle());
            dto.setDescription(chapter.getDescription());
        }

        if (sections.contains(ChapterVisibleSection.CHAPTER_DATES)) {
            dto.setStartDate(chapter.getStartDate());
            dto.setEndDate(chapter.getEndDate());
        }

        if (sections.contains(ChapterVisibleSection.HOTEL_SUMMARY)) {
            dto.setHotelSummary(hotel.map(this::toHotelSummaryDto).orElse(null));
        }

        if (sections.contains(ChapterVisibleSection.HOTEL_DETAILS)) {
            dto.setHotelDetails(hotel.map(this::toHotelDto).orElse(null));
        }

        if (sections.contains(ChapterVisibleSection.MAP)) {
            dto.setMap(hotel.map(this::toHotelSummaryDto).orElse(null));
        }

        if (sections.contains(ChapterVisibleSection.INNER_IMAGES)) {
            dto.setImages(
                    chapterImageRepository.findByChapterIdOrderBySortOrderAscCreatedAtAsc(chapter.getId())
                            .stream()
                            .map(chapterMapper::toImageResponseDto)
                            .toList()
            );
        }

        if (sections.contains(ChapterVisibleSection.ROOM_TYPE) && booking.isPresent()) {
            Booking b = booking.get();
            dto.setRoomType(roomTypeRepository.findById(b.getRoomTypeId()).map(this::toRoomTypeDto).orElse(null));
        }

        if (sections.contains(ChapterVisibleSection.PAYMENT_SUMMARY) && booking.isPresent()) {
            PaymentPlanResponseDto plan = paymentService.getPaymentPlanByBookingId(booking.get().getId());
            dto.setPaymentSummary(new ChapterPaymentSummaryDto(plan.getTotal(), plan.getCurrency()));
        }

        if (sections.contains(ChapterVisibleSection.BOOKING_DATES) && booking.isPresent()) {
            Booking b = booking.get();

            if (b.getCheckOutDate() != null && !b.getCheckOutDate().isAfter(LocalDate.now())) {
                dto.setCheckInDate(b.getCheckInDate());
                dto.setCheckOutDate(b.getCheckOutDate());
            }
        }

        if (sections.contains(ChapterVisibleSection.NEARBY_PLACES)) {
            dto.setNearbyPlaces(List.of());
        }

        if (sections.contains(ChapterVisibleSection.REVIEWS)) {
            dto.setReviews(List.of());
        }

        return dto;
    }

    private ChapterHotelDto toHotelDto(Hotel hotel) {
        ChapterHotelDto dto = toHotelSummaryDto(hotel);
        dto.setDescription(hotel.getDescription());
        return dto;
    }

    private ChapterHotelDto toHotelSummaryDto(Hotel hotel) {
        ChapterHotelDto dto = new ChapterHotelDto();
        dto.setId(hotel.getId());
        dto.setName(hotel.getName());
        dto.setCity(hotel.getCity());
        dto.setCountry(hotel.getCountry());
        dto.setRating(hotel.getRating());
        dto.setImageUrl(hotel.getImageUrl());
        return dto;
    }

    private ChapterRoomTypeDto toRoomTypeDto(RoomType roomType) {
        ChapterRoomTypeDto dto = new ChapterRoomTypeDto();
        dto.setId(roomType.getId());
        dto.setHotelId(roomType.getHotelId() != null ? roomType.getHotelId() : roomType.getHotel().getId());
        dto.setName(roomType.getName());
        dto.setCapacity(roomType.getCapacity());
        dto.setBasePrice(roomType.getBasePrice());
        dto.setImageUrl(roomType.getImageUrl());
        dto.setAmenities(roomType.getAmenities());
        return dto;
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("startDate and endDate are required");
        }

        if (!startDate.isBefore(endDate)) {
            throw new IllegalArgumentException("startDate must be before endDate");
        }
    }

    private void validateBookingForChapter(Booking booking, Long hotelId, Long guestId) {
        if (!hotelId.equals(booking.getHotelId())) {
            throw new IllegalArgumentException("Booking does not belong to the selected hotel");
        }

        if (booking.getGuest() == null || booking.getGuest().getId() == null || !guestId.equals(booking.getGuest().getId())) {
            throw new IllegalArgumentException("Booking does not belong to the chapter owner");
        }

        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Chapter can be created only for a confirmed booking");
        }
    }

    private void normalizeVisibleSections(Chapter chapter) {
        if (chapter.getVisibility() == null) {
            chapter.setVisibility(ChapterVisibility.PRIVATE);
        }

        if (chapter.getPublicVisibleSections() == null) {
            chapter.setPublicVisibleSections(new HashSet<>());
        }

        if (chapter.getVisibility() == ChapterVisibility.PUBLIC && chapter.getPublicVisibleSections().isEmpty()) {
            chapter.setPublicVisibleSections(new HashSet<>(DEFAULT_PUBLIC_SECTIONS));
        }

        if (chapter.getVisibility() == ChapterVisibility.PRIVATE) {
            chapter.setPublicVisibleSections(new HashSet<>());
        }
    }

    private Set<ChapterVisibleSection> visibleSectionsFor(Chapter chapter) {
        if (chapter.getPublicVisibleSections() == null || chapter.getPublicVisibleSections().isEmpty()) {
            return new HashSet<>(DEFAULT_PUBLIC_SECTIONS);
        }

        return new HashSet<>(chapter.getPublicVisibleSections());
    }

    private void ensureCanManage(Chapter chapter) {
        if (!isCurrentUserOwnerOrAdmin(chapter)) {
            throw new AccessDeniedException("Only owner or admin can manage this chapter");
        }
    }

    private Guest currentGuestOrThrow() {
        Long guestId = currentGuestIdOrNull();

        if (guestId == null) {
            throw new AccessDeniedException("Authenticated guest is required");
        }

        return guestRepository.findById(guestId)
                .orElseThrow(() -> new AccessDeniedException("Authenticated guest is required"));
    }

    private Long currentGuestIdOrThrow() {
        Long guestId = currentGuestIdOrNull();

        if (guestId == null) {
            throw new AccessDeniedException("Authenticated guest is required");
        }

        return guestId;
    }

    private Long currentGuestIdOrNull() {
        return currentUser()
                .flatMap(user -> guestRepository.findByUserId(user.getId()))
                .map(Guest::getId)
                .orElse(null);
    }

    private Optional<AppUser> currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            Number claimUserId = jwt.getClaim("userId");

            if (claimUserId != null) {
                return appUserRepository.findById(claimUserId.longValue());
            }
        }

        String login = authentication.getName();

        if (login == null || "anonymousUser".equals(login)) {
            return Optional.empty();
        }

        return appUserRepository.findByEmail(login).or(() -> appUserRepository.findByUsername(login));
    }

    private boolean isAdmin(Authentication authentication) {
        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}