package de.studyshare.studyshare.dto.entity;

import java.time.LocalDateTime;

import de.studyshare.studyshare.domain.Review;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
/**
 * Data Transfer Object for Review entities.
 * Contains review information for transfer between layers.
 */
public record ReviewDTO(
        /**
         * Unique identifier for the review.
         */
        Long id,
        /**
         * Rating given in the review, between 1 and 5 stars.
         */
        @Min(1) @Max(5) int stars,
        /**
         * Subject or title of the review.
         */
        String subject,
        /**
         * Detailed comment or content of the review.
         */
        String comment,
        /**
         * User who created the review.
         */
        UserDTO user,
        /**
         * ID of the content (e.g., course, book) that the review is associated with.
         */
        Long contentId,
        /**
         * Timestamp when the review was created.
         */
        LocalDateTime createdAt,
        /**
         * Timestamp when the review was last updated.
         */
        LocalDateTime updatedAt) {
    /**
     * Converts a Review entity to a ReviewDTO.
     * 
     * @param review the Review entity to convert
     * @return a ReviewDTO containing the review's information, or null if the
     *         review is null
     */
    public static ReviewDTO fromEntity(Review review) {
        if (review == null) {
            return null;
        }
        return new ReviewDTO(
                review.getId(),
                review.getStars(),
                review.getSubject(),
                review.getComment(),
                review.getUser() != null ? UserDTO.fromEntity(review.getUser()) : null,
                review.getContent() != null ? review.getContent().getId() : null,
                review.getCreatedAt(),
                review.getUpdatedAt());
    }
}
