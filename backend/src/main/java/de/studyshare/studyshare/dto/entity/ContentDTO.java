package de.studyshare.studyshare.dto.entity;

import java.time.LocalDate;

import de.studyshare.studyshare.domain.Content;
import de.studyshare.studyshare.domain.ContentCategory;

public record ContentDTO(
        Long id,
        UserDTO uploadedBy,
        int reportedCount,
        int outdatedCount,
        LocalDate uploadDate,
        ContentCategory contentCategory,
        LecturerDTO lecturer,
        CourseDTO course,
        FacultyDTO faculty,
        String filePath,
        String title
        ) {

    public static ContentDTO fromEntity(Content content) {
        if (content == null) {
            return null;
        }
        return new ContentDTO(
                content.getId(),
                content.getUploadedBy() != null ? UserDTO.fromEntity(content.getUploadedBy()) : null,
                content.getReportedCount(),
                content.getOutdatedCount(),
                content.getUploadDate(),
                content.getContentCategory(),
                content.getLecturer() != null ? LecturerDTO.fromEntity(content.getLecturer()) : null,
                content.getCourse() != null ? CourseDTO.fromEntity(content.getCourse()) : null,
                content.getFaculty() != null ? FacultyDTO.fromEntity(content.getFaculty()) : null,
                content.getFilePath(),
                content.getTitle()
        );
    }
}
