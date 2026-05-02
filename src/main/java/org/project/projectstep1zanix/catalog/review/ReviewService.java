package org.project.projectstep1zanix.catalog.review;

import java.util.List;

public interface ReviewService {
    ReviewResponseDto createReview(Long hotelId, ReviewRequestDto dto);
    List<ReviewResponseDto> getReviewsByHotelId(Long hotelId);
    ReviewSummaryDto getReviewSummary(Long hotelId);
    void deleteReview(Long hotelId, Long reviewId);
}
