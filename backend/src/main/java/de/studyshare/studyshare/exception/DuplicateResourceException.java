package de.studyshare.studyshare.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a bad request is made to the server.
 * This could be due to invalid input, missing parameters, or other client-side
 * errors.
 * The response status for this exception is set to 400 Bad Request.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends RuntimeException {
    /**
     * Constructs a new DuplicateResourceException with the specified detail
     * message.
     *
     * @param message the detail message
     */
    public DuplicateResourceException(String message) {
        super(message);
    }

    /**
     * Constructs a new DuplicateResourceException with a formatted message.
     *
     * @param resourceName the name of the resource that is duplicated
     * @param fieldName    the name of the field that caused the duplication
     * @param fieldValue   the value of the field that caused the duplication
     */
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s already exists with %s : '%s'", resourceName, fieldName, fieldValue));
    }
}
