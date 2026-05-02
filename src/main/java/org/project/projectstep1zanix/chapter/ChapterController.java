package org.project.projectstep1zanix.chapter;

import java.net.URI;
import java.util.List;

import org.project.projectstep1zanix.common.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ChapterController {

    private final ChapterService chapterService;

    public ChapterController(ChapterService chapterService) {
        this.chapterService = chapterService;
    }

    @Operation(summary = "Create chapter", description = "Creates a chapter for a confirmed booking owned by the authenticated guest.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Chapter created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid chapter data"),
            @ApiResponse(responseCode = "403", description = "Only guests can create chapters"),
            @ApiResponse(responseCode = "404", description = "Hotel or booking not found"),
            @ApiResponse(responseCode = "409", description = "Business rule conflict")
    })
    @PostMapping("/chapters")
    @PreAuthorize("hasRole('GUEST')")
    public ResponseEntity<ChapterResponseDto> createChapter(
            @Valid @RequestBody ChapterRequestDto request,
            UriComponentsBuilder uriBuilder
    ) {
        ChapterResponseDto responseDto = chapterService.createChapter(request);

        URI location = uriBuilder.path("/api/chapters/{id}")
                .buildAndExpand(responseDto.getId())
                .toUri();

        return ResponseEntity.created(location).body(responseDto);
    }

    @Operation(summary = "Get my chapters", description = "Returns chapters owned by the authenticated guest.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chapters retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Only guests can access their chapters")
    })
    @GetMapping("/chapters/me")
    @PreAuthorize("hasRole('GUEST')")
    public ResponseEntity<List<ChapterResponseDto>> getMyChapters() {
        List<ChapterResponseDto> responseDto = chapterService.getMyChapters();
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Get chapter by ID", description = "Owners/admins receive full chapter data. Public users receive only allowed public sections.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chapter retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Private chapter access denied"),
            @ApiResponse(responseCode = "404", description = "Chapter not found")
    })
    @GetMapping("/chapters/{id}")
    public ResponseEntity<Object> getChapterById(@PathVariable Long id) {
        Object responseDto = chapterService.getChapterById(id);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Update chapter", description = "Updates chapter metadata, visibility, dates, hotel/booking link, or visible sections.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chapter updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid chapter data"),
            @ApiResponse(responseCode = "403", description = "Only owner or admin can update this chapter"),
            @ApiResponse(responseCode = "404", description = "Chapter, hotel, or booking not found")
    })
    @PutMapping("/chapters/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManageChapter(authentication, #id)")
    public ResponseEntity<ChapterResponseDto> updateChapter(
            @PathVariable Long id,
            @Valid @RequestBody ChapterUpdateRequestDto request
    ) {
        ChapterResponseDto responseDto = chapterService.updateChapter(id, request);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Delete chapter", description = "Deletes a chapter and its stored images.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Chapter deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Only owner or admin can delete this chapter"),
            @ApiResponse(responseCode = "404", description = "Chapter not found")
    })
    @DeleteMapping("/chapters/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManageChapter(authentication, #id)")
    public ResponseEntity<Void> deleteChapter(@PathVariable Long id) {
        chapterService.deleteChapter(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "List public chapters", description = "Returns public chapters for sidebar/public stories with optional hotel filters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Public chapters retrieved successfully")
    })
    @GetMapping("/chapters/public")
    public ResponseEntity<PagedResponse<PublicChapterResponseDto>> getPublicChapters(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Double minRating,
            Pageable pageable
    ) {
        PagedResponse<PublicChapterResponseDto> responseDto =
                chapterService.getPublicChapters(country, city, minRating, pageable);

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Get public hotel stories", description = "Returns public chapters linked to one hotel.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hotel stories retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    @GetMapping("/hotels/{hotelId}/stories")
    public ResponseEntity<List<PublicChapterResponseDto>> getPublicHotelStories(@PathVariable Long hotelId) {
        List<PublicChapterResponseDto> responseDto = chapterService.getPublicHotelStories(hotelId);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Upload cover image", description = "Uploads a cover image used by chapter cards, sidebar, and public story lists.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cover image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid image file"),
            @ApiResponse(responseCode = "403", description = "Only owner or admin can upload cover image"),
            @ApiResponse(responseCode = "404", description = "Chapter not found")
    })
    @PostMapping(value = "/chapters/{chapterId}/cover-image", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManageChapter(authentication, #chapterId)")
    public ResponseEntity<ChapterResponseDto> uploadCoverImage(
            @PathVariable Long chapterId,
            @RequestParam("image") MultipartFile image
    ) {
        ChapterResponseDto responseDto = chapterService.uploadCoverImage(chapterId, image);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Update cover image", description = "Replaces the existing chapter cover image.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cover image updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid image file"),
            @ApiResponse(responseCode = "403", description = "Only owner or admin can update cover image"),
            @ApiResponse(responseCode = "404", description = "Chapter not found")
    })
    @PutMapping(value = "/chapters/{chapterId}/cover-image", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManageChapter(authentication, #chapterId)")
    public ResponseEntity<ChapterResponseDto> updateCoverImage(
            @PathVariable Long chapterId,
            @RequestParam("image") MultipartFile image
    ) {
        ChapterResponseDto responseDto = chapterService.updateCoverImage(chapterId, image);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Delete cover image", description = "Deletes the chapter cover image.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cover image deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Only owner or admin can delete cover image"),
            @ApiResponse(responseCode = "404", description = "Chapter not found")
    })
    @DeleteMapping("/chapters/{chapterId}/cover-image")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManageChapter(authentication, #chapterId)")
    public ResponseEntity<Void> deleteCoverImage(@PathVariable Long chapterId) {
        chapterService.deleteCoverImage(chapterId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Add inner chapter image", description = "Uploads an image displayed inside the chapter page.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inner image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid image file"),
            @ApiResponse(responseCode = "403", description = "Only owner or admin can add chapter images"),
            @ApiResponse(responseCode = "404", description = "Chapter not found")
    })
    @PostMapping(value = "/chapters/{chapterId}/images", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManageChapter(authentication, #chapterId)")
    public ResponseEntity<ChapterImageResponseDto> addImage(
            @PathVariable Long chapterId,
            @RequestParam("image") MultipartFile image,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer sortOrder
    ) {
        ChapterImageResponseDto responseDto =
                chapterService.addImage(chapterId, image, description, sortOrder);

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Update inner chapter image metadata", description = "Updates description or ordering for an inner chapter image.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image metadata updated successfully"),
            @ApiResponse(responseCode = "403", description = "Only owner or admin can update chapter images"),
            @ApiResponse(responseCode = "404", description = "Chapter or image not found")
    })
    @PutMapping("/chapters/{chapterId}/images/{imageId}")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManageChapter(authentication, #chapterId)")
    public ResponseEntity<ChapterImageResponseDto> updateImage(
            @PathVariable Long chapterId,
            @PathVariable Long imageId,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer sortOrder
    ) {
        ChapterImageResponseDto responseDto =
                chapterService.updateImage(chapterId, imageId, description, sortOrder);

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Delete inner chapter image", description = "Deletes an inner image from a chapter.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Image deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Only owner or admin can delete chapter images"),
            @ApiResponse(responseCode = "404", description = "Chapter or image not found")
    })
    @DeleteMapping("/chapters/{chapterId}/images/{imageId}")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManageChapter(authentication, #chapterId)")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long chapterId,
            @PathVariable Long imageId
    ) {
        chapterService.deleteImage(chapterId, imageId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update public visible sections", description = "Controls which sections are returned for public chapter reads.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Public visible sections updated successfully"),
            @ApiResponse(responseCode = "403", description = "Only owner or admin can update visible sections"),
            @ApiResponse(responseCode = "404", description = "Chapter not found")
    })
    @PatchMapping("/chapters/{chapterId}/public-sections")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManageChapter(authentication, #chapterId)")
    public ResponseEntity<ChapterResponseDto> updatePublicVisibleSections(
            @PathVariable Long chapterId,
            @Valid @RequestBody UpdateChapterVisibleSectionsRequestDto request
    ) {
        ChapterResponseDto responseDto =
                chapterService.updatePublicVisibleSections(chapterId, request);

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Get chapter visible section options", description = "Returns all public visibility section enum values for frontend checkboxes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Visible section options retrieved successfully")
    })
    @GetMapping("/chapters/visible-sections")
    public ResponseEntity<List<ChapterVisibleSection>> getVisibleSections() {
        List<ChapterVisibleSection> responseDto = chapterService.getVisibleSections();
        return ResponseEntity.ok(responseDto);
    }
}