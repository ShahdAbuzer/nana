package org.project.projectstep1zanix.chapter;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ChapterStoryService {

    ChapterStoryResponseDto createStory(Long hotelId, MultipartFile image);

    List<ChapterStoryResponseDto> getActiveHotelStories(Long hotelId);

    List<ChapterStoryResponseDto> getMyActiveStories();

    void deleteStory(Long storyId);

    void deleteExpiredStories();

    ChapterStory getStoryEntityById(Long storyId);

    boolean isCurrentUserOwnerOrAdmin(ChapterStory story);
}