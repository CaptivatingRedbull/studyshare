package de.studyshare.studyshare.dto.request;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CourseCreateRequest(
        @NotNull(message = "Course ID cannot be null")
        @NotBlank(message = "Course name cannot be blank")
        @Size(min = 2, max = 150, message = "Course name must be between 2 and 150 characters")
        String name,
        @NotNull(message = "Faculty ID cannot be null")
        Long facultyId,
        @NotNull(message = "Lecturer IDs cannot be null")
        Set<Long> lecturerIds
        ) {

}
