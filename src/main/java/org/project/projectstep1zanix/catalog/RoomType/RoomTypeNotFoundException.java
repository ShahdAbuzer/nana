package org.project.projectstep1zanix.catalog.RoomType;

public class RoomTypeNotFoundException extends RuntimeException {
    public RoomTypeNotFoundException(Long id) {
        super("RoomType not found with ID: " + id);
    }
}
