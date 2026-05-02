package org.project.projectstep1zanix.chapter;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.project.projectstep1zanix.Users.Guest;
import org.project.projectstep1zanix.Users.GuestRepository;
import org.project.projectstep1zanix.catalog.Hotel.HotelNotFoundException;
import org.project.projectstep1zanix.catalog.Hotel.HotelRepository;
import org.project.projectstep1zanix.file.FileStorageService;
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
public class ChapterStoryServiceImpl implements ChapterStoryService {

    private final ChapterStoryRepository chapterStoryRepository;
    private final ChapterStoryMapper chapterStoryMapper;
    private final HotelRepository hotelRepository;
    private final GuestRepository guestRepository;
    private final FileStorageService fileStorageService;

    public ChapterStoryServiceImpl(
            ChapterStoryRepository chapterStoryRepository,
            ChapterStoryMapper chapterStoryMapper,
            HotelRepository hotelRepository,
            GuestRepository guestRepository,
            FileStorageService fileStorageService
    ) {
        this.chapterStoryRepository = chapterStoryRepository;
        this.chapterStoryMapper = chapterStoryMapper;
        this.hotelRepository = hotelRepository;
        this.guestRepository = guestRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public ChapterStoryResponseDto createStory(Long hotelId, MultipartFile image) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new HotelNotFoundException(hotelId);
        }

        Guest currentGuest = currentGuestOrThrow();

        String imageUrl = fileStorageService.saveFile(image, "stories");

        ChapterStory story = new ChapterStory();
        story.setHotelId(hotelId);
        story.setGuestId(currentGuest.getId());
        story.setImageUrl(imageUrl);

        ChapterStory savedStory = chapterStoryRepository.save(story);
        return chapterStoryMapper.toResponseDto(savedStory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterStoryResponseDto> getActiveHotelStories(Long hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new HotelNotFoundException(hotelId);
        }

        return chapterStoryRepository
                .findByHotelIdAndExpiresAtAfterOrderByCreatedAtDesc(hotelId, Instant.now())
                .stream()
                .map(chapterStoryMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChapterStoryResponseDto> getMyActiveStories() {
        Long guestId = currentGuestIdOrThrow();

        return chapterStoryRepository
                .findByGuestIdAndExpiresAtAfterOrderByCreatedAtDesc(guestId, Instant.now())
                .stream()
                .map(chapterStoryMapper::toResponseDto)
                .toList();
    }

    @Override
    public void deleteStory(Long storyId) {
        ChapterStory story = getStoryEntityById(storyId);
        ensureCanManage(story);
        deleteStoryInternal(story);
    }

    @Override
    public void deleteExpiredStories() {
        List<ChapterStory> expiredStories = chapterStoryRepository.findByExpiresAtBefore(Instant.now());

        for (ChapterStory story : expiredStories) {
            deleteStoryInternal(story);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ChapterStory getStoryEntityById(Long storyId) {
        return chapterStoryRepository.findById(storyId)
                .orElseThrow(() -> new ChapterNotFoundException("Story not found with id: " + storyId));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isCurrentUserOwnerOrAdmin(ChapterStory story) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (hasRole(authentication, "ADMIN")) {
            return true;
        }

        Long currentGuestId = currentGuestIdOrNull();

        return story != null
                && currentGuestId != null
                && story.getGuestId() != null
                && story.getGuestId().equals(currentGuestId);
    }

    private void deleteStoryInternal(ChapterStory story) {
        if (story.getImageUrl() != null && !story.getImageUrl().isBlank()) {
            fileStorageService.deleteFile(story.getImageUrl());
        }

        chapterStoryRepository.delete(story);
    }

    private void ensureCanManage(ChapterStory story) {
        if (!isCurrentUserOwnerOrAdmin(story)) {
            throw new AccessDeniedException("Only story owner or admin can delete this story");
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        if ("anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            Number claimUserId = jwt.getClaim("userId");

            if (claimUserId != null) {
                return guestRepository.findByUserId(claimUserId.longValue())
                        .map(Guest::getId)
                        .orElse(null);
            }
        }

        String login = authentication.getName();

        if (login == null || login.isBlank()) {
            return null;
        }

        Optional<Guest> guestByUsername = guestRepository.findByUserUsername(login);

        if (guestByUsername.isPresent()) {
            return guestByUsername.get().getId();
        }

        return guestRepository.findByUserEmail(login)
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
                .map(GrantedAuthority::getAuthority)
                .anyMatch(expectedAuthority::equals);
    }
}