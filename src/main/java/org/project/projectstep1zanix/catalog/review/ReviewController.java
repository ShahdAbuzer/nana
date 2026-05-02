package org.project.projectstep1zanix.catalog.review;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/hotels/{hotelId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "Create review", description = "Creates a review for a hotel. User must have a confirmed booking.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Hotel not found"),
            @ApiResponse(responseCode = "409", description = "Already reviewed or no confirmed booking")
    })
    @PostMapping
    @PreAuthorize("hasRole('GUEST')")
    public ResponseEntity<ReviewResponseDto> create(
            @PathVariable Long hotelId,
            @Valid @RequestBody ReviewRequestDto dto) {
        ReviewResponseDto created = reviewService.createReview(hotelId, dto);
        return ResponseEntity.status(201).body(created);
    }

    @Operation(summary = "List reviews", description = "Lists all reviews for a hotel.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews retrieved")
    })
    @GetMapping
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<ReviewResponseDto>> getAll(@PathVariable Long hotelId) {
        return ResponseEntity.ok(reviewService.getReviewsByHotelId(hotelId));
    }

    @Operation(summary = "Review summary", description = "Returns average rating and total review count for a hotel.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Summary retrieved")
    })
    @GetMapping("/summary")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ReviewSummaryDto> getSummary(@PathVariable Long hotelId) {
        return ResponseEntity.ok(reviewService.getReviewSummary(hotelId));
    }

    @Operation(summary = "Delete review", description = "Deletes a review. Only the review owner or ADMIN can delete.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @DeleteMapping("/{reviewId}")
    @PreAuthorize("hasAnyRole('GUEST','ADMIN')")
    public ResponseEntity<Void> delete(
            @PathVariable Long hotelId,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(hotelId, reviewId);
        return ResponseEntity.noContent().build();
    }
}
