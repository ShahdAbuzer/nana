package org.project.projectstep1zanix.chapter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class PublicChapterResponseDto {

    private Long id;
    private String title;
    private String description;
    private String coverImageUrl;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Set<ChapterVisibleSection> visibleSections;
    private Instant createdAt;

    private ChapterHotelDto hotelSummary;
    private ChapterHotelDto hotelDetails;
    private ChapterHotelDto map;
    private ChapterRoomTypeDto roomType;
    private ChapterPaymentSummaryDto paymentSummary;
    private List<ChapterImageResponseDto> images;
    private List<Object> nearbyPlaces;
    private List<Object> reviews;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public Set<ChapterVisibleSection> getVisibleSections() {
        return visibleSections;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public ChapterHotelDto getHotelSummary() {
        return hotelSummary;
    }

    public ChapterHotelDto getHotelDetails() {
        return hotelDetails;
    }

    public ChapterHotelDto getMap() {
        return map;
    }

    public ChapterRoomTypeDto getRoomType() {
        return roomType;
    }

    public ChapterPaymentSummaryDto getPaymentSummary() {
        return paymentSummary;
    }

    public List<ChapterImageResponseDto> getImages() {
        return images;
    }

    public List<Object> getNearbyPlaces() {
        return nearbyPlaces;
    }

    public List<Object> getReviews() {
        return reviews;
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

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public void setVisibleSections(Set<ChapterVisibleSection> visibleSections) {
        this.visibleSections = visibleSections;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setHotelSummary(ChapterHotelDto hotelSummary) {
        this.hotelSummary = hotelSummary;
    }

    public void setHotelDetails(ChapterHotelDto hotelDetails) {
        this.hotelDetails = hotelDetails;
    }

    public void setMap(ChapterHotelDto map) {
        this.map = map;
    }

    public void setRoomType(ChapterRoomTypeDto roomType) {
        this.roomType = roomType;
    }

    public void setPaymentSummary(ChapterPaymentSummaryDto paymentSummary) {
        this.paymentSummary = paymentSummary;
    }

    public void setImages(List<ChapterImageResponseDto> images) {
        this.images = images;
    }

    public void setNearbyPlaces(List<Object> nearbyPlaces) {
        this.nearbyPlaces = nearbyPlaces;
    }

    public void setReviews(List<Object> reviews) {
        this.reviews = reviews;
    }
}