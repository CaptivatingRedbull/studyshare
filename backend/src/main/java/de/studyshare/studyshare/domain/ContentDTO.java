package de.studyshare.studyshare.domain;

import java.time.LocalDate;

public record ContentDTO(
        Long id,
        UserDTO uploadedBy,
        int reportedCount,
        int outdatedCount,
        LocalDate uploadDate,
        ContentCategory contentCategory,
        LecturerDTO lecturerDTO,
        CourseDTO courseDTO,
        FacultyDTO facultyDTO,
        String filePath
        ) {

}
