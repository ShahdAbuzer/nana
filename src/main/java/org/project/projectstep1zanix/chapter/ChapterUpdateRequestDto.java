package org.project.projectstep1zanix.chapter;

import java.time.LocalDate;
import java.util.Set;

public class ChapterUpdateRequestDto {

    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private ChapterVisibility visibility;
    private Long hotelId;
    private Long bookingId;
    private Set<ChapterVisibleSection> publicVisibleSections;

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

    public Set<ChapterVisibleSection> getPublicVisibleSections() {
        return publicVisibleSections;
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

    public void setPublicVisibleSections(Set<ChapterVisibleSection> publicVisibleSections) {
        this.publicVisibleSections = publicVisibleSections;
    }
}