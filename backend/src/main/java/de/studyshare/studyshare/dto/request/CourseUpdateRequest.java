package de.studyshare.studyshare.dto.request;

import java.util.Set;

import jakarta.validation.constraints.Size;

public record CourseUpdateRequest(
        @Size(min = 2, max = 150, message = "Course name must be between 2 and 150 characters")
        String name,
        Long facultyId,
        Set<Long> lecturerIds
        ) {

}
