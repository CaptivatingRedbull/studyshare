package de.studyshare.studyshare.service;

import de.studyshare.studyshare.domain.Review;
import de.studyshare.studyshare.domain.ReviewDTO;
import de.studyshare.studyshare.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    public List<ReviewDTO> getAllReviewsForContent(Long contentId) {
        return reviewRepository.findByContentId(contentId)
                               .stream()
                               .map(Review::toDTO)
                               .toList();
    }

    @Transactional(readOnly = true)
    public ReviewDTO getReviewById(Long reviewId) {
        Review r = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new IllegalArgumentException("Review not found: " + reviewId));
        return r.toDTO();
    }

    @Transactional
    public ReviewDTO createReview(Review review) {
        Review saved = reviewRepository.save(review);
        return saved.toDTO();
    }

    @Transactional
    public ReviewDTO updateReview(Long id, Review updated) {
        Review existing = reviewRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Review not found: " + id));
        existing.setStars(updated.getStars());
        existing.setSubject(updated.getSubject());
        existing.setComment(updated.getComment());
        // if you allow reassigning user/content, handle here...
        Review saved = reviewRepository.save(existing);
        return saved.toDTO();
    }

    @Transactional
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}