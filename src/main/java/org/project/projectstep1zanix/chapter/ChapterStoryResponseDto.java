package org.project.projectstep1zanix.chapter;

import java.time.Instant;

public class ChapterStoryResponseDto {

    private Long id;
    private Long hotelId;
    private Long guestId;
    private String imageUrl;
    private Instant createdAt;
    private Instant expiresAt;

    public Long getId() {
        return id;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public Long getGuestId() {
        return guestId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}