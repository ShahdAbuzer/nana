package org.project.projectstep1zanix.catalog.review;

import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public Review toEntity(ReviewRequestDto dto, Long hotelId, Long guestId) {
        Review review = new Review();
        review.setHotelId(hotelId);
        review.setGuestId(guestId);
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        return review;
    }

    public ReviewResponseDto toDto(Review entity) {
        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setId(entity.getId());
        dto.setHotelId(entity.getHotelId());
        dto.setGuestId(entity.getGuestId());
        dto.setRating(entity.getRating());
        dto.setComment(entity.getComment());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }
}
