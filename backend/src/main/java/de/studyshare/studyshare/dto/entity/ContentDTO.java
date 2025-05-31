package de.studyshare.studyshare.dto.entity;

import java.time.LocalDate;

import de.studyshare.studyshare.domain.Content;
import de.studyshare.studyshare.domain.ContentCategory;

/**
 * Data Transfer Object for Content entities.
 * Represents content information for transfer between layers without exposing
 * domain implementation details.
 */
public record ContentDTO(
        /**
         * Unique identifier for the content.
         */
        Long id,

        /**
         * The user who uploaded this content.
         */
        UserDTO uploadedBy,

        /**
         * Number of times this content has been reported by users.
         */
        int reportedCount,

        /**
         * Number of times this content has been marked as outdated.
         */
        int outdatedCount,

        /**
         * Date when the content was uploaded.
         */
        LocalDate uploadDate,

        /**
         * Category of the content (e.g., LECTURE_NOTES, EXERCISE, EXAM).
         */
        ContentCategory contentCategory,

        /**
         * The lecturer associated with this content.
         */
        LecturerDTO lecturer,

        /**
         * The course associated with this content.
         */
        CourseDTO course,

        /**
         * The faculty associated with this content.
         */
        FacultyDTO faculty,

        /**
         * The file path to the content resource.
         */
        String filePath,

        /**
         * Title of the content.
         */
        String title,

        /**
         * Average rating of the content based on user reviews.
         */
        Double averageRating) {

    /**
     * Converts a Content entity to a ContentDTO.
     *
     * @param content The Content entity to convert
     * @return A new ContentDTO with data from the entity, or null if the input is
     *         null
     */
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
                content.getTitle(),
                content.getAverageRating());
    }
}