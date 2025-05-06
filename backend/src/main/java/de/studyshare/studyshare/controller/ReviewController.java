package de.studyshare.studyshare.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import de.studyshare.studyshare.domain.Review;
import de.studyshare.studyshare.domain.ReviewDTO;
import de.studyshare.studyshare.service.ReviewService;

@RestController
@RequestMapping("/api/contents/{contentId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<ReviewDTO> listForContent(@PathVariable Long contentId) {
        return reviewService.getAllReviewsForContent(contentId);
    }

    @GetMapping("/{reviewId}")
    public ReviewDTO getOne(@PathVariable Long reviewId) {
        return reviewService.getReviewById(reviewId);
    }

    @PostMapping
    public ResponseEntity<ReviewDTO> create(
            @RequestBody Review review) {
        ReviewDTO dto = reviewService.createReview(review);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{reviewId}")
    public ReviewDTO update(
            @PathVariable Long reviewId,
            @RequestBody Review payload) {
        return reviewService.updateReview(reviewId, payload);
    }

    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
    }
}