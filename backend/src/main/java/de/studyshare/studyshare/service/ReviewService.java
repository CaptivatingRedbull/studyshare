package de.studyshare.studyshare.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.studyshare.studyshare.domain.Content;
import de.studyshare.studyshare.domain.Review;
import de.studyshare.studyshare.domain.Role;
import de.studyshare.studyshare.domain.User;
import de.studyshare.studyshare.dto.entity.ReviewDTO;
import de.studyshare.studyshare.dto.request.ReviewCreateRequest;
import de.studyshare.studyshare.dto.request.ReviewUpdateRequest;
import de.studyshare.studyshare.exception.BadRequestException;
import de.studyshare.studyshare.exception.ResourceNotFoundException;
import de.studyshare.studyshare.repository.ContentRepository;
import de.studyshare.studyshare.repository.ReviewRepository;
import de.studyshare.studyshare.repository.UserRepository;

/**
 * Service class for managing reviews of content.
 * Provides methods to create, update, delete, and retrieve reviews.
 */
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;

    /**
     * Constructs a ReviewService with the specified repositories.
     *
     * @param reviewRepository  the repository for managing reviews
     * @param userRepository    the repository for managing users
     * @param contentRepository the repository for managing content
     */
    public ReviewService(ReviewRepository reviewRepository,
            UserRepository userRepository,
            ContentRepository contentRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
    }

    /**
     * Retrieves all reviews for a specific content item.
     *
     * @param contentId the ID of the content to retrieve reviews for
     * @return a list of ReviewDTO objects representing the reviews for the content
     * @throws ResourceNotFoundException if no content with the given ID exists
     */
    @Transactional(readOnly = true)
    public List<ReviewDTO> getAllReviewsForContent(Long contentId) {
        if (!contentRepository.existsById(contentId)) {
            throw new ResourceNotFoundException("Content", "id", contentId);
        }
        return reviewRepository.findByContentId(contentId)
                .stream()
                .map(ReviewDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a review by its ID.
     *
     * @param reviewId the ID of the review to retrieve
     * @return a ReviewDTO object representing the review
     * @throws ResourceNotFoundException if no review with the given ID exists
     */
    @Transactional(readOnly = true)
    public ReviewDTO getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .map(ReviewDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
    }

    /**
     * Creates a new review for a specific content item.
     *
     * @param contentId     the ID of the content to review
     * @param createRequest the request containing the details of the review to
     *                      create
     * @return a ReviewDTO object representing the created review
     * @throws ResourceNotFoundException if no content with the given ID exists
     * @throws BadRequestException       if the user tries to review their own
     *                                   content or has already reviewed it
     */
    @Transactional
    public ReviewDTO createReview(Long contentId, ReviewCreateRequest createRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User reviewingUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username",
                        currentUsername + " (authenticated user not found)"));

        Content contentToReview = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", contentId));

        if (contentToReview.getUploadedBy().getId().equals(reviewingUser.getId())) {
            throw new BadRequestException("Users cannot review their own content.");
        }

        if (reviewRepository.existsByContentIdAndUserId(contentId, reviewingUser.getId())) {
            throw new BadRequestException("You have already reviewed this content.");
        }

        Review review = new Review();
        review.setUser(reviewingUser);
        review.setContent(contentToReview);
        review.setStars(createRequest.stars());
        review.setSubject(createRequest.subject());
        review.setComment(createRequest.comment());

        Review savedReview = reviewRepository.save(review);
        
        //calling update avg rating to keep it uptodate 
        updateContentAverageRating(contentId);
        return ReviewDTO.fromEntity(savedReview);
    }

    /**
     * Updates an existing review.
     *
     * @param reviewId      the ID of the review to update
     * @param updateRequest the request containing the updated details of the review
     * @return a ReviewDTO object representing the updated review
     * @throws ResourceNotFoundException if no review with the given ID exists
     * @throws AccessDeniedException     if the user is not authorized to update the
     *                                   review
     */
    @Transactional
    public ReviewDTO updateReview(Long reviewId, ReviewUpdateRequest updateRequest) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        if (!review.getUser().getUsername().equals(currentUsername)
                && !(userRepository.findByUsername(authentication.getName())
                        .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername))
                        .getRole() == Role.ADMIN)) {
            throw new AccessDeniedException("You are not authorized to update this review.");
        }

        if (updateRequest.stars() != null) {
            review.setStars(updateRequest.stars());
        }
        if (updateRequest.subject() != null) {
            review.setSubject(updateRequest.subject());
        }
        if (updateRequest.comment() != null) {
            review.setComment(updateRequest.comment());
        }

        Review updatedReview = reviewRepository.save(review);

        //calling update avg rating to keep it uptodate 
        updateContentAverageRating(review.getContent().getId());
        return ReviewDTO.fromEntity(updatedReview);
    }

    /**
     * Deletes a review by its ID.
     *
     * @param reviewId the ID of the review to delete
     * @throws ResourceNotFoundException if no review with the given ID exists
     * @throws AccessDeniedException     if the user is not authorized to delete the
     *                                   review
     */
    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        if (!review.getUser().getUsername().equals(currentUsername) &&
                !(userRepository.findByUsername(authentication.getName())
                        .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername))
                        .getRole() == Role.ADMIN)) {
            throw new AccessDeniedException("You are not authorized to delete this review.");
        }
        Long contentId = review.getContent().getId();
        reviewRepository.delete(review);
        updateContentAverageRating(contentId);
    }

    /**
     * Updates the average rating of the content based on its reviews.
     *
     * @param contentId the ID of the content to update the average rating for
     * @throws ResourceNotFoundException if no content with the given ID exists
     */
    private void updateContentAverageRating(Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id",
                        contentId + " (while updating average rating)"));

        List<Review> reviews = reviewRepository.findByContentId(contentId);
        if (reviews.isEmpty()) {
            content.setAverageRating(0.0);
        } else {
            double average = reviews.stream()
                    .mapToInt(Review::getStars)
                    .average()
                    .orElse(0.0);
            average = Math.round(average * 10.0) / 10.0;
            content.setAverageRating(average);
        }
        contentRepository.save(content);
    }
}
