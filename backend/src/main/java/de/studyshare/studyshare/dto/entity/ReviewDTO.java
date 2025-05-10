package de.studyshare.studyshare.dto.entity;

import java.time.LocalDateTime;

import de.studyshare.studyshare.domain.Review;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ReviewDTO(
        Long id,
        @Min(1)
        @Max(5)
        int stars,
        String subject,
        String comment,
        UserDTO user,
        Long contentId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
        ) {

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
                review.getUpdatedAt()
        );
    }
}
