package de.studyshare.studyshare.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotNull(message = "FirstName must not be null") @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters") String firstName,
        @NotNull(message = "LastName must not be null") @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters") String lastName,
        @NotNull(message = "Email must not be null") @Email(message = "Email should be valid") @Size(max = 100, message = "Email must be less than 100 characters") String email) {

}
