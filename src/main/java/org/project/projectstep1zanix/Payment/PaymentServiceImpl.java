package org.project.projectstep1zanix.Payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.project.projectstep1zanix.booking.Booking;
import org.project.projectstep1zanix.booking.BookingNotFoundException;
import org.project.projectstep1zanix.booking.BookingRepository;
import org.project.projectstep1zanix.booking.BookingStatus;
import org.project.projectstep1zanix.catalog.RoomType.RoomType;
import org.project.projectstep1zanix.catalog.RoomType.RoomTypeNotFoundException;
import org.project.projectstep1zanix.catalog.RoomType.RoomTypeRepository;
import org.project.projectstep1zanix.common.PagedResponse;
import org.project.projectstep1zanix.notification.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final BookingRepository bookingRepository;
    private final RoomTypeRepository roomTypeRepository;
     private final NotificationService notificationService;


    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            PaymentMapper paymentMapper,
            BookingRepository bookingRepository,
            RoomTypeRepository roomTypeRepository,
            NotificationService notificationService
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.bookingRepository = bookingRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.notificationService = notificationService;
    }
@Override
public PaymentResponseDto createPaymentIntent(PaymentRequestDto request) {
    if (request == null) {
        throw new InvalidPaymentException("Payment request must not be null.");
    }
    if (request.getBookingId() == null) {
        throw new InvalidPaymentException("bookingId is required.");
    }

    Booking booking = bookingRepository.findById(request.getBookingId())
            .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + request.getBookingId()));

    if (booking.getStatus() == BookingStatus.CANCELLED) {
        throw new InvalidPaymentException("Cannot create payment for a cancelled booking.");
    }

    if (booking.getTotalPrice() == null) {
        throw new InvalidPaymentException("Booking total price is missing.");
    }

    boolean alreadyPaid = paymentRepository.existsByBookingIdAndStatusIn(
            booking.getId(),
            List.of(PaymentStatus.PENDING, PaymentStatus.SUCCEEDED, PaymentStatus.REFUNDED)
    );

    if (alreadyPaid) {
        throw new PaymentConflictException("A payment already exists for this booking.");
    }

    String currency = request.getCurrency();
    if (currency == null || currency.isBlank()) {
        currency = "USD";
    } else {
        currency = currency.trim().toUpperCase();
    }

    Payment payment = new Payment();
    payment.setBookingId(booking.getId());
    payment.setAmount(BigDecimal.valueOf(booking.getTotalPrice()).setScale(2, RoundingMode.HALF_UP));
    payment.setCurrency(currency);
    payment.setStatus(PaymentStatus.PENDING);
    payment.setProviderReference("pi_" + UUID.randomUUID().toString().replace("-", ""));
    payment.setFailureReason(null);
    payment.setRefundedAmount(null);

    Payment saved = paymentRepository.save(payment);
    return paymentMapper.toDto(saved);
}

    @Override
public PaymentResponseDto simulateSuccess(Long paymentId) {
    Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException(paymentId));

    if (payment.getStatus() == PaymentStatus.REFUNDED) {
        throw new PaymentConflictException("Refunded payment cannot be marked as successful.");
    }

    if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
        throw new PaymentConflictException("Payment is already successful.");
    }

    Booking booking = bookingRepository.findById(payment.getBookingId())
            .orElseThrow(() -> new BookingNotFoundException(
                    "Booking not found with id: " + payment.getBookingId()
            ));

    payment.setStatus(PaymentStatus.SUCCEEDED);
    payment.setFailureReason(null);
    Payment saved = paymentRepository.save(payment);

    booking.setStatus(BookingStatus.CONFIRMED);
    bookingRepository.save(booking);

    notificationService.sendPaymentSuccessEmail(
            booking.getGuest().getUser().getEmail(),
            booking.getGuest().getUser().getUsername(),
            booking.getId()
    );

    return paymentMapper.toDto(saved);
}
    @Override
    public PaymentResponseDto simulateFailure(Long paymentId, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new PaymentConflictException("Refunded payment cannot be marked as failed.");
        }

        if (payment.getStatus() == PaymentStatus.SUCCEEDED) {
            throw new PaymentConflictException("Successful payment cannot be failed.");
        }

        if (reason == null || reason.isBlank()) {
            reason = "Payment failed.";
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason.trim());

        Payment saved = paymentRepository.save(payment);
        return paymentMapper.toDto(saved);
    }

  @Override
public PaymentResponseDto refundPayment(Long bookingId) {

    if (bookingId == null) {
        throw new InvalidPaymentException("bookingId is required for refund.");
    }

    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new BookingNotFoundException(
                    "Booking not found with id: " + bookingId
            ));

    if (booking.getStatus() != BookingStatus.CANCELLED) {
        throw new InvalidPaymentException("Refund allowed only after booking is cancelled.");
    }

    Payment successfulPayment = paymentRepository
            .findTopByBookingIdAndStatusOrderByCreatedAtDesc(bookingId, PaymentStatus.SUCCEEDED)
            .orElse(null);

    if (successfulPayment != null) {
        successfulPayment.setStatus(PaymentStatus.REFUNDED);
        successfulPayment.setRefundedAmount(successfulPayment.getAmount());
        successfulPayment.setFailureReason(null);

        Payment saved = paymentRepository.save(successfulPayment);

        notificationService.sendPaymentRefundEmail(
                booking.getGuest().getUser().getEmail(),
                booking.getGuest().getUser().getUsername(),
                booking.getId()
        );

        return paymentMapper.toDto(saved);
    }

    Payment alreadyRefundedPayment = paymentRepository
            .findTopByBookingIdAndStatusOrderByCreatedAtDesc(bookingId, PaymentStatus.REFUNDED)
            .orElse(null);

    if (alreadyRefundedPayment != null) {
        throw new PaymentConflictException("Payment already refunded.");
    }

    Payment latestPayment = paymentRepository
            .findTopByBookingIdOrderByCreatedAtDesc(bookingId)
            .orElseThrow(() -> new PaymentNotFoundException(
                    "No payment found for booking id: " + bookingId
            ));

    if (latestPayment.getStatus() == PaymentStatus.PENDING) {
        throw new PaymentConflictException("Pending payment cannot be refunded. Mark it as successful first.");
    }

    if (latestPayment.getStatus() == PaymentStatus.FAILED) {
        throw new PaymentConflictException("Failed payment cannot be refunded.");
    }

    throw new PaymentConflictException("Only successful payments can be refunded.");
}

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));

        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getLatestPaymentByBookingId(Long bookingId) {
        Payment payment = paymentRepository.findTopByBookingIdOrderByCreatedAtDesc(bookingId)
                .orElseThrow(() -> new PaymentNotFoundException("No payment found for booking id: " + bookingId));

        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PaymentResponseDto> searchPayments(
            Long bookingId,
            PaymentStatus status,
            String currency,
            String providerReference,
            Pageable pageable
    ) {
        Specification<Payment> spec = Specification
                .where(PaymentSpecifications.hasBookingId(bookingId))
                .and(PaymentSpecifications.hasStatus(status))
                .and(PaymentSpecifications.hasCurrency(currency))
                .and(PaymentSpecifications.hasProviderReference(providerReference));

        Page<PaymentResponseDto> page = paymentRepository.findAll(spec, pageable)
                .map(paymentMapper::toDto);

        return PagedResponse.from(page, page.getContent());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PaymentResponseDto> findAll(Pageable pageable) {
        return paymentRepository.findAll(pageable)
                .map(paymentMapper::toDto);
    }
    @Override
@Transactional(readOnly = true)
public Payment getPaymentEntityById(Long paymentId) {
    return paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException(paymentId));
}
@Override
@Transactional(readOnly = true)
public PaymentPlanResponseDto getPaymentPlanByBookingId(Long bookingId) {
    Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

    RoomType roomType = roomTypeRepository.findById(booking.getRoomTypeId())
            .orElseThrow(() -> new RoomTypeNotFoundException(booking.getRoomTypeId()));

    long nights = 0;
    if (booking.getCheckInDate() != null && booking.getCheckOutDate() != null) {
        nights = ChronoUnit.DAYS.between(booking.getCheckInDate(), booking.getCheckOutDate());
    }

    BigDecimal pricePerNight = roomType.getBasePrice() == null
            ? BigDecimal.ZERO
            : BigDecimal.valueOf(roomType.getBasePrice()).setScale(2, RoundingMode.HALF_UP);

    BigDecimal subtotal = pricePerNight
            .multiply(BigDecimal.valueOf(Math.max(nights, 0)))
            .setScale(2, RoundingMode.HALF_UP);

    BigDecimal tax = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    BigDecimal serviceFee = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
    BigDecimal discount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    Payment latestPayment = paymentRepository.findTopByBookingIdOrderByCreatedAtDesc(bookingId)
            .orElse(null);

    BigDecimal total = booking.getTotalPrice() == null
            ? subtotal
            : BigDecimal.valueOf(booking.getTotalPrice()).setScale(2, RoundingMode.HALF_UP);

    String currency = "USD";
    PaymentStatus paymentStatus = null;

    if (latestPayment != null) {
        if (latestPayment.getAmount() != null) {
            total = latestPayment.getAmount();
        }
        if (latestPayment.getCurrency() != null) {
            currency = latestPayment.getCurrency();
        }
        paymentStatus = latestPayment.getStatus();
    }

    PaymentPlanResponseDto dto = new PaymentPlanResponseDto();
    dto.setBookingId(booking.getId());
    dto.setHotelId(booking.getHotelId());
    dto.setRoomTypeId(booking.getRoomTypeId());
    dto.setCheckInDate(booking.getCheckInDate());
    dto.setCheckOutDate(booking.getCheckOutDate());
    dto.setNights(nights);
    dto.setPricePerNight(pricePerNight);
    dto.setSubtotal(subtotal);
    dto.setTax(tax);
    dto.setServiceFee(serviceFee);
    dto.setDiscount(discount);
    dto.setTotal(total);
    dto.setCurrency(currency);
    dto.setPaymentStatus(paymentStatus);

    return dto;
}
}