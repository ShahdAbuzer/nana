package org.project.projectstep1zanix.chapter;

import java.util.List;

import org.project.projectstep1zanix.common.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ChapterService {

    ChapterResponseDto createChapter(ChapterRequestDto request);

    List<ChapterResponseDto> getMyChapters();

    Object getChapterById(Long id);

    ChapterResponseDto updateChapter(Long id, ChapterUpdateRequestDto request);

    void deleteChapter(Long id);

    ChapterResponseDto uploadCoverImage(Long chapterId, MultipartFile image);

    ChapterResponseDto updateCoverImage(Long chapterId, MultipartFile image);

    void deleteCoverImage(Long chapterId);

    ChapterImageResponseDto addImage(
            Long chapterId,
            MultipartFile image,
            String description,
            Integer sortOrder
    );

    ChapterImageResponseDto updateImage(
            Long chapterId,
            Long imageId,
            String description,
            Integer sortOrder
    );

    void deleteImage(Long chapterId, Long imageId);

    ChapterResponseDto updatePublicVisibleSections(
            Long chapterId,
            UpdateChapterVisibleSectionsRequestDto request
    );

    List<ChapterVisibleSection> getVisibleSections();

    PagedResponse<PublicChapterResponseDto> getPublicChapters(
            String country,
            String city,
            Double minRating,
            Pageable pageable
    );

    List<PublicChapterResponseDto> getPublicHotelStories(Long hotelId);

    Chapter getChapterEntityById(Long id);

    boolean isCurrentUserOwnerOrAdmin(Chapter chapter);
}