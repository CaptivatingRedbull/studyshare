package de.studyshare.studyshare.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a bad request is made to the server.
 * This could be due to invalid input, missing parameters, or other client-side
 * errors.
 * The response status for this exception is set to 400 Bad Request.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new ResourceNotFoundException with a formatted message.
     *
     * @param resourceName the name of the resource that was not found
     * @param fieldName    the name of the field that was searched
     * @param fieldValue   the value of the field that was searched
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
    }
}
