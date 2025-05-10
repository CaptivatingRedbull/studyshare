package de.studyshare.studyshare.dto.request;

import de.studyshare.studyshare.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,
        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,
        @Email(message = "Email should be valid")
        @Size(max = 100, message = "Email must be less than 100 characters")
        String email,
        Role role
        ) {

}
