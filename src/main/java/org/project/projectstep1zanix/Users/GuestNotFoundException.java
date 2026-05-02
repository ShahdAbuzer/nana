package org.project.projectstep1zanix.Users;

public class GuestNotFoundException extends RuntimeException {
    public GuestNotFoundException(Long value) {
        super("Guest not found for value: " + value);
    }

    public GuestNotFoundException(String message) {
        super(message);
    }
}