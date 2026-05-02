package org.project.projectstep1zanix.availability_pricing.Availability;
public class AvailabilityNotFoundException extends RuntimeException {

    public AvailabilityNotFoundException(Long id) {
        super("Availability not found with id: " + id);
    }

    public AvailabilityNotFoundException(String message) {
        super(message);
    }
}