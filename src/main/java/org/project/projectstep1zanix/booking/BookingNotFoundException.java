package org.project.projectstep1zanix.booking;

public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(Long id) {
        super("Booking not found with ID: " + id);
    }

    public BookingNotFoundException(String message) {
        super(message);
    }
}