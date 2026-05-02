package org.project.projectstep1zanix.chapter;

import java.util.Set;

import jakarta.validation.constraints.NotNull;

public class UpdateChapterVisibleSectionsRequestDto {

    @NotNull
    private Set<ChapterVisibleSection> publicVisibleSections;

    public Set<ChapterVisibleSection> getPublicVisibleSections() {
        return publicVisibleSections;
    }

    public void setPublicVisibleSections(Set<ChapterVisibleSection> publicVisibleSections) {
        this.publicVisibleSections = publicVisibleSections;
    }
}