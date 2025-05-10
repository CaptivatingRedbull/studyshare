package de.studyshare.studyshare.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import de.studyshare.studyshare.dto.entity.ReviewDTO;
import de.studyshare.studyshare.dto.request.ReviewCreateRequest;
import de.studyshare.studyshare.dto.request.ReviewUpdateRequest;
import de.studyshare.studyshare.service.ReviewService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/contents/{contentId}/reviews") // Base path for reviews of a specific content
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReviewDTO>> getAllReviewsForContent(@PathVariable Long contentId) {
        List<ReviewDTO> reviews = reviewService.getAllReviewsForContent(contentId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long contentId, @PathVariable Long reviewId) {
        ReviewDTO review = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(review);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReviewDTO> createReview(@PathVariable Long contentId,
            @Valid @RequestBody ReviewCreateRequest createRequest) {
        ReviewDTO createdReview = reviewService.createReview(contentId, createRequest);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{reviewId}")
                .buildAndExpand(createdReview.id())
                .toUri();
        return ResponseEntity.created(location).body(createdReview);
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()") // Further authorization in service layer
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable Long contentId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest updateRequest) {
        ReviewDTO updatedReview = reviewService.updateReview(reviewId, updateRequest);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{reviewId}")
    @PreAuthorize("isAuthenticated()") // Further authorization in service layer
    public ResponseEntity<Void> deleteReview(@PathVariable Long contentId,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
