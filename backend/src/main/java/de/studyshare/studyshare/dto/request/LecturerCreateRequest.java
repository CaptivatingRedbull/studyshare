package de.studyshare.studyshare.dto.request;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LecturerCreateRequest(
        @NotBlank(message = "Lecturer name cannot be blank")
        @Size(min = 2, max = 100, message = "Lecturer name must be between 2 and 100 characters")
        String name,
        @Email(message = "Email should be valid")
        @Size(max = 100, message = "Email must be less than 100 characters")
        String email,
        Set<Long> courseIds
        ) {

}
