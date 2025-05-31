package de.studyshare.studyshare.dto.request;

import de.studyshare.studyshare.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a new user.
 * Contains necessary fields to create a user in the system.
 */
public record UserCreateRequest(
        /**
         * The first name of the user being created.
         * Cannot be null, must not be blank, and must be between 2 and 50 characters.
         */
        @NotNull(message = "First name cannot be null") @NotBlank(message = "First name cannot be blank") @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters") String firstName,
        /**
         * The last name of the user being created.
         * Cannot be null, must not be blank, and must be between 2 and 50 characters.
         */
        @NotNull(message = "Last name cannot be null") @NotBlank(message = "Last name cannot be blank") @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters") String lastName,
        /**
         * The email of the user being created.
         * Must be a valid email format, cannot be null, must not be blank,
         * and must not exceed 100 characters.
         */
        @NotNull(message = "Email cannot be null") @NotBlank(message = "Email cannot be blank") @Email(message = "Email should be valid") @Size(max = 100, message = "Email must be less than 100 characters") String email,
        /**
         * The username of the user being created.
         * Cannot be null, must not be blank, and must be between 3 and 30 characters.
         */
        @NotNull(message = "Username cannot be null") @NotBlank(message = "Username cannot be blank") @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters") String username,
        /**
         * The password of the user being created.
         * Cannot be null, must not be blank, and must be between 8 and 100 characters.
         */
        @NotNull(message = "Password cannot be null") @NotBlank(message = "Password cannot be blank") @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters") String password,
        /**
         * The role of the user being created.
         * Cannot be null.
         */
        @NotNull(message = "Role cannot be null") Role role) {

}
