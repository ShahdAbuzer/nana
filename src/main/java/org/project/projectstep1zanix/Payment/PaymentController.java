package org.project.projectstep1zanix.Payment;

import org.project.projectstep1zanix.common.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
@Operation(summary = "Create payment intent", description = "Creates a payment request for the selected booking.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Payment intent created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid payment request"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Booking not found")
})
    @PostMapping("/intents")
   
@PreAuthorize("hasRole('ADMIN') or @authz.canAccessPaymentByBooking(authentication, #request.bookingId)")
    public ResponseEntity<PaymentResponseDto> createPaymentIntent(
            @Valid @RequestBody PaymentRequestDto request
    ) {
        PaymentResponseDto response = paymentService.createPaymentIntent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
@Operation(summary = "Mark payment as successful", description = "Marks the payment as successful and confirms the related booking.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment marked as successful"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Payment not found")
})
    @PutMapping("/{paymentId}/success")
   @PreAuthorize("hasRole('ADMIN') or @authz.canManagePayment(authentication, #paymentId) or @authz.canAccessPayment(authentication, #paymentId)")
    public ResponseEntity<PaymentResponseDto> simulateSuccess(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.simulateSuccess(paymentId));
    }
@Operation(summary = "Mark payment as failed", description = "Marks the payment as failed.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment marked as failed"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Payment not found")
})
    @PutMapping("/{paymentId}/failure")
    @PreAuthorize("hasRole('ADMIN') or @authz.canManagePayment(authentication, #paymentId) or @authz.canAccessPayment(authentication, #paymentId)")
    public ResponseEntity<PaymentResponseDto> simulateFailure(
            @PathVariable Long paymentId,
            @Valid @RequestBody PaymentFailureRequestDto request
    ) {
        return ResponseEntity.ok(paymentService.simulateFailure(paymentId, request.getReason()));
    }
@Operation(summary = "Mark payment as failed", description = "Marks the payment as failed.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment marked as failed"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Payment not found")
})
    @PostMapping("/bookings/{bookingId}/refund")
@PreAuthorize("hasRole('ADMIN') or @authz.canManagePaymentByBooking(authentication, #bookingId)")
    public ResponseEntity<PaymentResponseDto> refundPayment(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.refundPayment(bookingId));
    }
@Operation(summary = "Get payment by ID", description = "Retrieves payment details by its ID.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Payment not found")
})
    @GetMapping("/{paymentId}")
   @PreAuthorize("hasRole('ADMIN') or @authz.canManagePayment(authentication, #paymentId) or @authz.canAccessPayment(authentication, #paymentId)")
    public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable Long paymentId) {
        return ResponseEntity.ok(paymentService.getPaymentById(paymentId));
    }
@Operation(summary = "Get latest payment by booking ID", description = "Retrieves the latest payment record for a booking.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Payment or booking not found")
})
    @GetMapping("/booking/{bookingId}")
   @PreAuthorize("hasRole('ADMIN') or @authz.canManagePaymentByBooking(authentication, #bookingId) or @authz.canAccessPaymentByBooking(authentication, #bookingId)")
    public ResponseEntity<PaymentResponseDto> getLatestPaymentByBookingId(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.getLatestPaymentByBookingId(bookingId));
    }
@Operation(summary = "Search payments", description = "Searches payments using optional filters.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
})
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<PagedResponse<PaymentResponseDto>> searchPayments(
            @RequestParam(required = false) Long bookingId,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) String providerReference,
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(
                paymentService.searchPayments(
                        bookingId,
                        status,
                        currency,
                        providerReference,
                        pageable
                )
        );
    }
@Operation(summary = "Search payments", description = "Searches payments using optional filters.")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
})
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Page<PaymentResponseDto>> findAll(
            @PageableDefault(size = 10, sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(paymentService.findAll(pageable));
    }
    @Operation(
        summary = "Get payment plan by booking ID",
        description = "Returns booking price breakdown for chapter/payment summary screens."
)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment plan retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied"),
        @ApiResponse(responseCode = "404", description = "Booking not found")
})
@GetMapping("/booking/{bookingId}/plan")
@PreAuthorize("hasRole('ADMIN') or @authz.canManagePaymentByBooking(authentication, #bookingId) or @authz.canAccessPaymentByBooking(authentication, #bookingId)")
public ResponseEntity<PaymentPlanResponseDto> getPaymentPlanByBookingId(@PathVariable Long bookingId) {
    return ResponseEntity.ok(paymentService.getPaymentPlanByBookingId(bookingId));
}
}