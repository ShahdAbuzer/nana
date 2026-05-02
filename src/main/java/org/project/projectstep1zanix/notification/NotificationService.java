package org.project.projectstep1zanix.notification;

public interface NotificationService {

    void sendPaymentSuccessEmail(String toEmail, String username, Long bookingId);

    void sendBookingConfirmation(String toEmail, String hotelName);

    void sendBookingCancellation(String toEmail, String hotelName);

    void sendPaymentRefundEmail(String toEmail, String username, Long bookingId);

}
