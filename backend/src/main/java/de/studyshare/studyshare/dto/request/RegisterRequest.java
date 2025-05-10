package de.studyshare.studyshare.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "First name cannot be blank")
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,
        @NotBlank(message = "Last name cannot be blank")
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email should be valid")
        @Size(max = 100, message = "Email must be less than 100 characters")
        String email,
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
        String username,
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String password
        ) {

}
