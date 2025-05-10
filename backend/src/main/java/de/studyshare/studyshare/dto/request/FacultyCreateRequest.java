package de.studyshare.studyshare.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FacultyCreateRequest(
        @NotBlank(message = "Faculty name cannot be blank")
        @Size(min = 3, max = 100, message = "Faculty name must be between 3 and 100 characters")
        String name
        ) {

}
