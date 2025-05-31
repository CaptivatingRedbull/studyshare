package de.studyshare.studyshare.exception;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Global exception handler for the StudyShare application.
 * This class handles various exceptions thrown by the application and returns
 * appropriate HTTP responses.
 * It uses Spring's @ControllerAdvice to intercept exceptions globally.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles ResourceNotFoundException and returns a 404 Not Found response.
     *
     * @param ex      the exception thrown
     * @param request the web request
     * @return ResponseEntity with error details and HTTP status 404
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        logger.error("ResourceNotFoundException: {}", ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    /**
     * Handles BadRequestException and returns a 400 Bad Request response.
     *
     * @param ex      the exception thrown
     * @param request the web request
     * @return ResponseEntity with error details and HTTP status 400
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException ex, WebRequest request) {
        logger.error("BadRequestException: {}", ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles DuplicateResourceException and returns a 409 Conflict response.
     *
     * @param ex      the exception thrown
     * @param request the web request
     * @return ResponseEntity with error details and HTTP status 409
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<?> handleDuplicateResourceException(DuplicateResourceException ex, WebRequest request) {
        logger.error("DuplicateResourceException: {}", ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    /**
     * Handles MethodArgumentNotValidException and returns a 400 Bad Request
     * response
     * with validation errors.
     *
     * @param ex      the exception thrown
     * @param request the web request
     * @return ResponseEntity with error details and HTTP status 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(
                error -> errors.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; "));
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                "Validation failed: " + errors.toString(),
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles all other exceptions that are not specifically handled.
     * Returns a 500 Internal Server Error response with the exception message.
     *
     * @param ex      the exception thrown
     * @param request the web request
     * @return ResponseEntity with error details and HTTP status 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Unhandled Exception: {}", ex.getMessage(), ex);
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "An unexpected error occurred: " + ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles AccessDeniedException and returns a 403 Forbidden response.
     *
     * @param ex      the exception thrown
     * @param request the web request
     * @return ResponseEntity with error details and HTTP status 403
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        logger.error("AccessDeniedException: {}", ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Access denied: " + ex.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles IllegalArgumentException and returns a 400 Bad Request response.
     *
     * @param ex      the exception thrown
     * @param request the web request
     * @return ResponseEntity with error details and HTTP status 400
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        logger.error("IllegalArgumentException: {}", ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * ErrorDetails class to encapsulate error information.
     * Contains timestamp, message, and details about the error.
     */
    public static class ErrorDetails {

        private final Date timestamp;
        private final String message;
        private final String details;

        /**
         * Constructs a new ErrorDetails instance.
         *
         * @param timestamp the time when the error occurred
         * @param message   a brief message describing the error
         * @param details   additional details about the error
         */
        public ErrorDetails(Date timestamp, String message, String details) {
            this.timestamp = timestamp;
            this.message = message;
            this.details = details;
        }

        /**
         * Gets the timestamp of when the error occurred.
         * 
         * @return the timestamp of the error
         */
        public Date getTimestamp() {
            return timestamp;
        }

        /**
         * Gets the message describing the error.
         * 
         * @return the error message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Gets additional details about the error.
         * 
         * @return the error details
         */
        public String getDetails() {
            return details;
        }
    }
}
