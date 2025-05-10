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
import de.studyshare.studyshare.domain.User;
import de.studyshare.studyshare.dto.entity.ReviewDTO;
import de.studyshare.studyshare.dto.request.ReviewCreateRequest;
import de.studyshare.studyshare.dto.request.ReviewUpdateRequest;
import de.studyshare.studyshare.exception.BadRequestException;
import de.studyshare.studyshare.exception.ResourceNotFoundException;
import de.studyshare.studyshare.repository.ContentRepository;
import de.studyshare.studyshare.repository.ReviewRepository;
import de.studyshare.studyshare.repository.UserRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ContentRepository contentRepository;

    public ReviewService(ReviewRepository reviewRepository,
            UserRepository userRepository,
            ContentRepository contentRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.contentRepository = contentRepository;
    }

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

    @Transactional(readOnly = true)
    public ReviewDTO getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .map(ReviewDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
    }

    @Transactional
    public ReviewDTO createReview(Long contentId, ReviewCreateRequest createRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User reviewingUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", currentUsername + " (authenticated user not found)"));

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
        return ReviewDTO.fromEntity(savedReview);
    }

    @Transactional
    public ReviewDTO updateReview(Long reviewId, ReviewUpdateRequest updateRequest) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        if (!review.getUser().getUsername().equals(currentUsername) /* && !isAdmin(authentication) */) {
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
        return ReviewDTO.fromEntity(updatedReview);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        if (!review.getUser().getUsername().equals(currentUsername) /* && !isAdmin(authentication) && !isContentOwner(review.getContent(), currentUsername) */) {
            throw new AccessDeniedException("You are not authorized to delete this review.");
        }

        reviewRepository.delete(review);
    }
}
