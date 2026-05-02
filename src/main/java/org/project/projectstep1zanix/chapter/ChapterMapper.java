package org.project.projectstep1zanix.chapter;

import java.util.HashSet;

import org.springframework.stereotype.Component;

@Component
public class ChapterMapper {

    public Chapter toEntity(ChapterRequestDto dto) {
        Chapter chapter = new Chapter();
        chapter.setTitle(dto.getTitle());
        chapter.setDescription(dto.getDescription());
        chapter.setStartDate(dto.getStartDate());
        chapter.setEndDate(dto.getEndDate());
        chapter.setVisibility(dto.getVisibility());
        chapter.setHotelId(dto.getHotelId());
        chapter.setBookingId(dto.getBookingId());

        if (dto.getPublicVisibleSections() != null) {
            chapter.setPublicVisibleSections(new HashSet<>(dto.getPublicVisibleSections()));
        }

        return chapter;
    }

    public ChapterResponseDto toResponseDto(Chapter chapter) {
        ChapterResponseDto dto = new ChapterResponseDto();
        dto.setId(chapter.getId());
        dto.setTitle(chapter.getTitle());
        dto.setDescription(chapter.getDescription());
        dto.setStartDate(chapter.getStartDate());
        dto.setEndDate(chapter.getEndDate());
        dto.setVisibility(chapter.getVisibility());
        dto.setHotelId(chapter.getHotelId());
        dto.setBookingId(chapter.getBookingId());
        dto.setGuestId(chapter.getGuestId());
        dto.setCoverImageUrl(chapter.getCoverImageUrl());
        dto.setPublicVisibleSections(chapter.getPublicVisibleSections());
        dto.setCreatedAt(chapter.getCreatedAt());
        dto.setUpdatedAt(chapter.getUpdatedAt());
        return dto;
    }

    public ChapterImageResponseDto toImageResponseDto(ChapterImage image) {
        ChapterImageResponseDto dto = new ChapterImageResponseDto();
        dto.setId(image.getId());
        dto.setChapterId(image.getChapterId());
        dto.setImageUrl(image.getImageUrl());
        dto.setDescription(image.getDescription());
        dto.setSortOrder(image.getSortOrder());
        dto.setCreatedAt(image.getCreatedAt());
        return dto;
    }
}