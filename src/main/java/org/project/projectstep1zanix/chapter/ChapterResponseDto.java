package org.project.projectstep1zanix.chapter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.project.projectstep1zanix.Payment.PaymentPlanResponseDto;

public class ChapterResponseDto {

    private Long id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private ChapterVisibility visibility;
    private Long hotelId;
    private Long bookingId;
    private Long guestId;
    private String coverImageUrl;
    private Set<ChapterVisibleSection> publicVisibleSections;
    private Instant createdAt;
    private Instant updatedAt;

    private ChapterHotelDto hotel;
    private ChapterRoomTypeDto roomType;
    private PaymentPlanResponseDto paymentPlan;
    private List<ChapterImageResponseDto> images;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ChapterVisibility getVisibility() {
        return visibility;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public Long getGuestId() {
        return guestId;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public Set<ChapterVisibleSection> getPublicVisibleSections() {
        return publicVisibleSections;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public ChapterHotelDto getHotel() {
        return hotel;
    }

    public ChapterRoomTypeDto getRoomType() {
        return roomType;
    }

    public PaymentPlanResponseDto getPaymentPlan() {
        return paymentPlan;
    }

    public List<ChapterImageResponseDto> getImages() {
        return images;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setVisibility(ChapterVisibility visibility) {
        this.visibility = visibility;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public void setPublicVisibleSections(Set<ChapterVisibleSection> publicVisibleSections) {
        this.publicVisibleSections = publicVisibleSections;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setHotel(ChapterHotelDto hotel) {
        this.hotel = hotel;
    }

    public void setRoomType(ChapterRoomTypeDto roomType) {
        this.roomType = roomType;
    }

    public void setPaymentPlan(PaymentPlanResponseDto paymentPlan) {
        this.paymentPlan = paymentPlan;
    }

    public void setImages(List<ChapterImageResponseDto> images) {
        this.images = images;
    }
}