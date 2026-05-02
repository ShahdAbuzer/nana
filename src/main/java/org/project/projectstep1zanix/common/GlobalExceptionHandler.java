package org.project.projectstep1zanix.common;

import java.util.LinkedHashMap;
import java.util.Map;

import org.project.projectstep1zanix.Payment.InvalidPaymentException;
import org.project.projectstep1zanix.Payment.PaymentConflictException;
import org.project.projectstep1zanix.Payment.PaymentNotFoundException;
import org.project.projectstep1zanix.Security.InvalidCredentialsException;
import org.project.projectstep1zanix.Security.InvalidRefreshTokenException;
import org.project.projectstep1zanix.Users.GuestNotFoundException;
import org.project.projectstep1zanix.availability_pricing.Availability.AvailabilityConflictException;
import org.project.projectstep1zanix.availability_pricing.Availability.AvailabilityNotFoundException;
import org.project.projectstep1zanix.availability_pricing.Availability.InvalidAvailabilityRequestException;
import org.project.projectstep1zanix.availability_pricing.Availability.InvalidDateRangeException;
import org.project.projectstep1zanix.availability_pricing.Pricing.InvalidPricingRuleException;
import org.project.projectstep1zanix.availability_pricing.Pricing.PricingRuleConflictException;
import org.project.projectstep1zanix.availability_pricing.Pricing.PricingRuleNotFoundException;
import org.project.projectstep1zanix.booking.BookingNotFoundException;
import org.project.projectstep1zanix.catalog.Hotel.HotelNotFoundException;
import org.project.projectstep1zanix.catalog.RoomType.RoomTypeNotFoundException;
import org.project.projectstep1zanix.chapter.ChapterNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ApiError build(HttpStatus status, String message, HttpServletRequest request) {
        return ApiError.of(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
    }

    private ApiError build(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> validationErrors
    ) {
        return ApiError.of(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                validationErrors
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public org.springframework.http.ResponseEntity<ApiError> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> validationErrors = new LinkedHashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            validationErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return org.springframework.http.ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(build(HttpStatus.BAD_REQUEST, "Validation failed", request, validationErrors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public org.springframework.http.ResponseEntity<ApiError> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        Map<String, String> validationErrors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(v ->
                validationErrors.put(v.getPropertyPath().toString(), v.getMessage())
        );

        return org.springframework.http.ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(build(HttpStatus.BAD_REQUEST, "Validation failed", request, validationErrors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public org.springframework.http.ResponseEntity<ApiError> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        String message = "Malformed JSON request";

        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                message = "Invalid value for field '%s'. Allowed values: %s"
                        .formatted(
                                ife.getPath().isEmpty() ? "unknown" : ife.getPath().get(0).getFieldName(),
                                java.util.Arrays.toString(ife.getTargetType().getEnumConstants())
                        );
            }
        }

        return org.springframework.http.ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(build(HttpStatus.BAD_REQUEST, message, request));
    }

    @ExceptionHandler({
            InvalidDateRangeException.class,
            InvalidAvailabilityRequestException.class,
            InvalidPricingRuleException.class,
            InvalidPaymentException.class,
            BadRequestException.class,
            IllegalArgumentException.class
    })
    public org.springframework.http.ResponseEntity<ApiError> handleBadRequest(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        return org.springframework.http.ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(build(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
    }

    @ExceptionHandler({
            InvalidCredentialsException.class,
            InvalidRefreshTokenException.class,
            AuthenticationCredentialsNotFoundException.class
    })
    public org.springframework.http.ResponseEntity<ApiError> handleUnauthorized(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        return org.springframework.http.ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request));
    }

    @ExceptionHandler({
            DuplicateResourceException.class,
            DataIntegrityViolationException.class
    })
    public org.springframework.http.ResponseEntity<ApiError> handleConflict(
            Exception ex,
            HttpServletRequest request
    ) {
        String message = ex instanceof DuplicateResourceException
                ? ex.getMessage()
                : "Duplicate value or database constraint violation";

        return org.springframework.http.ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(build(HttpStatus.CONFLICT, message, request));
    }

    @ExceptionHandler({
            AvailabilityConflictException.class,
            PricingRuleConflictException.class,
            PaymentConflictException.class,
            IllegalStateException.class
    })
    public org.springframework.http.ResponseEntity<ApiError> handleBusinessConflict(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        return org.springframework.http.ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(build(HttpStatus.CONFLICT, ex.getMessage(), request));
    }

    @ExceptionHandler({
            AvailabilityNotFoundException.class,
            PricingRuleNotFoundException.class,
            PaymentNotFoundException.class,
            BookingNotFoundException.class,
            GuestNotFoundException.class,
            HotelNotFoundException.class,
            RoomTypeNotFoundException.class,
            ResourceNotFoundException.class,
               ChapterNotFoundException.class
    })
    public org.springframework.http.ResponseEntity<ApiError> handleNotFound(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        return org.springframework.http.ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(build(HttpStatus.NOT_FOUND, ex.getMessage(), request));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public org.springframework.http.ResponseEntity<ApiError> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        return org.springframework.http.ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(build(HttpStatus.FORBIDDEN, "Access denied", request));
    }

    @ExceptionHandler(ErrorResponseException.class)
    public org.springframework.http.ResponseEntity<ApiError> handleErrorResponseException(
            ErrorResponseException ex,
            HttpServletRequest request
    ) {
        HttpStatusCode code = ex.getStatusCode();
        HttpStatus status = HttpStatus.valueOf(code.value());

        return org.springframework.http.ResponseEntity
                .status(status)
                .body(build(status, ex.getBody().getDetail(), request));
    }

    @ExceptionHandler(Exception.class)
    public org.springframework.http.ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        return org.springframework.http.ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(build(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request));
    }
}