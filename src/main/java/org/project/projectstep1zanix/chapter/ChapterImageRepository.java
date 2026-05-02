package org.project.projectstep1zanix.chapter;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterImageRepository extends JpaRepository<ChapterImage, Long> {

    List<ChapterImage> findByChapterIdOrderBySortOrderAscCreatedAtAsc(Long chapterId);

    Optional<ChapterImage> findByIdAndChapterId(Long id, Long chapterId);

    void deleteByChapterId(Long chapterId);
}