package de.studyshare.studyshare.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating an existing user.
 * Contains necessary fields to update a user's information in the system.
 */
public record UserUpdateRequest(
        /**
         * The first name of the user being updated.
         * Cannot be null, must be between 2 and 50 characters.
         */
        @NotNull(message = "FirstName must not be null") @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters") String firstName,
        /**
         * The last name of the user being updated.
         * Cannot be null, must be between 2 and 50 characters.
         */
        @NotNull(message = "LastName must not be null") @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters") String lastName,
        /**
         * The email of the user being updated.
         * Must be a valid email format, cannot be null, and must not exceed 100
         * characters.
         */
        @NotNull(message = "Email must not be null") @Email(message = "Email should be valid") @Size(max = 100, message = "Email must be less than 100 characters") String email) {

}
