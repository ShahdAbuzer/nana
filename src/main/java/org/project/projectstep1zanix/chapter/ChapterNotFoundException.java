package org.project.projectstep1zanix.chapter;

public class ChapterNotFoundException extends RuntimeException {

    public ChapterNotFoundException(Long id) {
        super("Chapter not found with id: " + id);
    }

    public ChapterNotFoundException(String message) {
        super(message);
    }
}