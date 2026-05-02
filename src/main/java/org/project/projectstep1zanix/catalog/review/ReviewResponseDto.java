package org.project.projectstep1zanix.catalog.review;

import java.time.LocalDateTime;

public class ReviewResponseDto {

    private Long id;
    private Long hotelId;
    private Long guestId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;

    public ReviewResponseDto() {
    }

    public ReviewResponseDto(Long id, Long hotelId, Long guestId, Integer rating,
                             String comment, LocalDateTime createdAt) {
        this.id = id;
        this.hotelId = hotelId;
        this.guestId = guestId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public Long getGuestId() {
        return guestId;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
