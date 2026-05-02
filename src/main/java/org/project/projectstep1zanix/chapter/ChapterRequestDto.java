package org.project.projectstep1zanix.chapter;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ChapterRequestDto {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    private ChapterVisibility visibility;

    @NotNull
    private Long hotelId;

    @NotNull
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