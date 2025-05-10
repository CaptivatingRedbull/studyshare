package de.studyshare.studyshare.dto.request;

import de.studyshare.studyshare.domain.ContentCategory;
import jakarta.validation.constraints.Size;

public record ContentUpdateRequest(
        ContentCategory contentCategory,
        Long courseId,
        Long lecturerId,
        Long facultyId,
        @Size(max = 255, message = "File path is too long")
        String filePath,
        @Size(max = 255, message = "Title or description is too long")
        String title
        ) {

}
