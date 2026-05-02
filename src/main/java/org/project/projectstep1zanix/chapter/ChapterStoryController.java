package org.project.projectstep1zanix.chapter;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
public class ChapterStoryController {

    private final ChapterStoryService chapterStoryService;

    public ChapterStoryController(ChapterStoryService chapterStoryService) {
        this.chapterStoryService = chapterStoryService;
    }

    @Operation(
            summary = "Create temporary hotel story",
            description = "Uploads a temporary story image for a hotel. The story expires after 24 hours."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Story created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid image file"),
            @ApiResponse(responseCode = "403", description = "Only guests can create stories"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    @PostMapping(value = "/api/hotels/{hotelId}/temporary-stories", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('GUEST')")
    public ResponseEntity<ChapterStoryResponseDto> createStory(
            @PathVariable Long hotelId,
            @RequestParam("image") MultipartFile image,
            UriComponentsBuilder uriBuilder
    ) {
        ChapterStoryResponseDto created = chapterStoryService.createStory(hotelId, image);

        URI location = uriBuilder
                .path("/api/hotels/{hotelId}/temporary-stories")
                .buildAndExpand(hotelId)
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @Operation(
            summary = "Get active hotel temporary stories",
            description = "Returns only non-expired stories for the selected hotel."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stories retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Hotel not found")
    })
    @GetMapping("/api/hotels/{hotelId}/temporary-stories")
    public ResponseEntity<List<ChapterStoryResponseDto>> getActiveHotelStories(@PathVariable Long hotelId) {
        return ResponseEntity.ok(chapterStoryService.getActiveHotelStories(hotelId));
    }

    @Operation(
            summary = "Get my active temporary stories",
            description = "Returns active stories uploaded by the authenticated guest."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stories retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Only guests can access their stories")
    })
    @GetMapping("/api/temporary-stories/me")
    @PreAuthorize("hasRole('GUEST')")
    public ResponseEntity<List<ChapterStoryResponseDto>> getMyActiveStories() {
        return ResponseEntity.ok(chapterStoryService.getMyActiveStories());
    }

    @Operation(
            summary = "Delete temporary story",
            description = "Deletes a story. Only the story owner or admin can delete it."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Story deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Only owner or admin can delete this story"),
            @ApiResponse(responseCode = "404", description = "Story not found")
    })
    @DeleteMapping("/api/temporary-stories/{storyId}")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManageTemporaryStory(authentication, #storyId)")
    public ResponseEntity<Void> deleteStory(@PathVariable Long storyId) {
        chapterStoryService.deleteStory(storyId);
        return ResponseEntity.noContent().build();
    }
}