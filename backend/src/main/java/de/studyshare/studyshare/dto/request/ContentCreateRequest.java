package de.studyshare.studyshare.dto.request;

import de.studyshare.studyshare.domain.ContentCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ContentCreateRequest(
        @NotNull(message = "Content category cannot be null")
        ContentCategory contentCategory,
        @NotNull(message = "Course ID cannot be null")
        Long courseId,
        @NotNull(message = "Lecturer ID cannot be null")
        Long lecturerId,
        @NotNull(message = "Faculty ID cannot be null")
        Long facultyId,
        @NotNull(message = "File path cannot be null")
        @NotBlank(message = "File path cannot be blank")
        @Size(max = 255, message = "File path is too long")
        String filePath,
        @NotNull(message = "Title or description cannot be null")
        @NotBlank(message = "Title or description cannot be blank")
        @Size(max = 255, message = "Title or description is too long")
        String title
        ) {

}
