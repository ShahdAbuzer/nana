package org.project.projectstep1zanix.chapter;

import java.time.Instant;

public class ChapterImageResponseDto {

    private Long id;
    private Long chapterId;
    private String imageUrl;
    private String description;
    private Integer sortOrder;
    private Instant createdAt;

    public Long getId() {
        return id;
    }

    public Long getChapterId() {
        return chapterId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}