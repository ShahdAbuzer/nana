package org.project.projectstep1zanix.chapter;

import org.springframework.stereotype.Component;

@Component
public class ChapterStoryMapper {

    public ChapterStoryResponseDto toResponseDto(ChapterStory story) {
        ChapterStoryResponseDto dto = new ChapterStoryResponseDto();

        dto.setId(story.getId());
        dto.setHotelId(story.getHotelId());
        dto.setGuestId(story.getGuestId());
        dto.setImageUrl(story.getImageUrl());
        dto.setCreatedAt(story.getCreatedAt());
        dto.setExpiresAt(story.getExpiresAt());

        return dto;
    }
}