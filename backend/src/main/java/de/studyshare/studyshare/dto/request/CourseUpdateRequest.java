package de.studyshare.studyshare.dto.request;

import java.util.Set;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CourseUpdateRequest(
        @NotNull(message = "Course ID cannot be null")
        @Size(min = 2, max = 150, message = "Course name must be between 2 and 150 characters")
        String name,
        @NotNull(message = "Faculty ID cannot be null")
        Long facultyId,
        @NotNull(message = "Lecturer IDs cannot be null")
        Set<Long> lecturerIds
        ) {

}
