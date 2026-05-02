package org.project.projectstep1zanix.chapter;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StoryCleanupScheduler {

    private final ChapterStoryService chapterStoryService;

    public StoryCleanupScheduler(ChapterStoryService chapterStoryService) {
        this.chapterStoryService = chapterStoryService;
    }

    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void deleteExpiredStories() {
        chapterStoryService.deleteExpiredStories();
    }
}