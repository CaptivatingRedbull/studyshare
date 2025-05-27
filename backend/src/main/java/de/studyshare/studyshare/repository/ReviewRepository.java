package de.studyshare.studyshare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.studyshare.studyshare.domain.Review;

/**
 * Repository interface for managing Review entities.
 * Provides methods to perform CRUD operations and custom queries on Review
 * data.
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Finds all reviews associated with a specific content ID.
     *
     * @param contentId the ID of the content to filter reviews by
     * @return a list of Review entities associated with the specified content ID
     */
    List<Review> findByContentId(Long contentId);

    /**
     * Checks if a review exists for a specific content ID and user ID.
     *
     * @param contentId the ID of the content
     * @param userId    the ID of the user
     * @return true if a review exists for the specified content ID and user ID,
     *         false otherwise
     */
    boolean existsByContentIdAndUserId(Long contentId, Long userId);

    /**
     * Deletes all reviews associated with a specific content ID.
     *
     * @param contentId the ID of the content whose reviews are to be deleted
     */
    void deleteByContentId(Long contentId);
}
