package org.project.projectstep1zanix.notification;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service

public class EmailNotificationService implements NotificationService {

    private final JavaMailSender mailSender;

   public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    @Override
    public void sendPaymentSuccessEmail(String toEmail, String username, Long bookingId) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("Payment Confirmed - Booking #" + bookingId);

        message.setText(
                "Hello " + username + ",\n\n" +
                "Your hotel payment has been completed successfully.\n" +
                "Your booking ID is: " + bookingId + ".\n\n" +
                "Thank you for using our hotel booking system.\n\n" +
                "Best regards,\n" +
                "Hotel Booking Team"
        );

        mailSender.send(message);
    }

    @Override
public void sendBookingConfirmation(String toEmail, String hotelName) {
    SimpleMailMessage message = new SimpleMailMessage();

    message.setTo(toEmail);
    message.setSubject("Booking Confirmed");

    message.setText(
            "Hello,\n\n" +
            "Your booking at " + hotelName + " has been confirmed successfully.\n\n" +
            "Best regards,\n" +
            "Hotel Booking Team"
    );

    mailSender.send(message);
}

@Override
public void sendBookingCancellation(String toEmail, String hotelName) {
    SimpleMailMessage message = new SimpleMailMessage();

    message.setTo(toEmail);
    message.setSubject("Booking Cancelled");

    message.setText(
            "Hello,\n\n" +
            "Your booking at " + hotelName + " has been cancelled successfully.\n\n" +
            "Best regards,\n" +
            "Hotel Booking Team"
    );

    mailSender.send(message);
}

@Override
public void sendPaymentRefundEmail(String toEmail, String username, Long bookingId) {
    SimpleMailMessage message = new SimpleMailMessage();

    message.setTo(toEmail);
    message.setSubject("Payment Refunded - Booking #" + bookingId);

    message.setText(
            "Hello " + username + ",\n\n" +
            "Your payment has been refunded successfully.\n" +
            "Booking ID: " + bookingId + "\n\n" +
            "Best regards,\n" +
            "Hotel Booking Team"
    );

    mailSender.send(message);
}


}