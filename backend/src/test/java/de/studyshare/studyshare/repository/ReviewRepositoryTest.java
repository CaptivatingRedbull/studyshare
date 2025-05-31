package de.studyshare.studyshare.repository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.studyshare.studyshare.AbstractDatabaseIntegrationTest;
import de.studyshare.studyshare.domain.Content;
import de.studyshare.studyshare.domain.ContentCategory;
import de.studyshare.studyshare.domain.Review;
import de.studyshare.studyshare.domain.Role;
import de.studyshare.studyshare.domain.User;

@DataJpaTest
public class ReviewRepositoryTest extends AbstractDatabaseIntegrationTest{

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReviewRepository reviewRepository;

    private User user;
    private Content content;
    private Review review;

    @TestConfiguration
    @SuppressWarnings("unused")
    static class TestConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @BeforeEach
    public void setup() {
        // Create a user
        user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("password123hash");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(Role.STUDENT);
        entityManager.persist(user);
        
        // Create content
        content = new Content();
        content.setTitle("Test Content");
        content.setFilePath("/path/to/file.pdf");
        content.setContentCategory(ContentCategory.PDF);
        content.setUploadedBy(user);
        content.setUploadDate(LocalDate.now());
        content.setReportedCount(0);
        content.setOutdatedCount(0);
        entityManager.persist(content);
        
        // Create a review
        review = new Review();
        review.setStars(5);
        review.setSubject("Great content"); // Setting the required subject field
        review.setComment("This content was very helpful for my studies.");
        review.setUser(user);
        review.setContent(content);
        entityManager.persist(review);
        
        entityManager.flush();
    }

    @Test
    @DisplayName("Should save and retrieve a review")
    public void shouldSaveAndRetrieveReview() {
        // Create a new review
        Review newReview = new Review();
        newReview.setStars(4);
        newReview.setSubject("Good resource"); // Important: set the subject
        newReview.setComment("This is a detailed comment about the content.");
        newReview.setUser(user);
        newReview.setContent(content);
        
        // Save the review
        Review savedReview = reviewRepository.save(newReview);
        
        // Retrieve the review
        Review foundReview = reviewRepository.findById(savedReview.getId()).orElse(null);
        
        // Verify
        assertThat(foundReview).isNotNull();
        assertThat(foundReview.getStars()).isEqualTo(4);
        assertThat(foundReview.getSubject()).isEqualTo("Good resource");
        assertThat(foundReview.getComment()).isEqualTo("This is a detailed comment about the content.");
        assertThat(foundReview.getUser().getId()).isEqualTo(user.getId());
        assertThat(foundReview.getContent().getId()).isEqualTo(content.getId());
        assertThat(foundReview.getCreatedAt()).isNotNull();
        assertThat(foundReview.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find reviews by content ID")
    public void shouldFindReviewsByContentId() {
        // Create another review for the same content
        Review anotherReview = new Review();
        anotherReview.setStars(3);
        anotherReview.setSubject("Average content"); // Required field
        anotherReview.setComment("It was okay but could be better.");
        anotherReview.setUser(user);
        anotherReview.setContent(content);
        entityManager.persist(anotherReview);
        entityManager.flush();
        
        // Find reviews by content ID
        List<Review> reviews = reviewRepository.findByContentId(content.getId());
        
        // Verify
        assertThat(reviews).isNotNull();
        assertThat(reviews).hasSize(2); // The one from setup and the one we just created
        assertThat(reviews.stream().map(Review::getSubject))
            .contains("Great content", "Average content");
    }

    @Test
    @DisplayName("Should check if review exists by content ID and user ID")
    public void shouldCheckIfReviewExistsByContentIdAndUserId() {
        // Check if review exists
        boolean exists = reviewRepository.existsByContentIdAndUserId(content.getId(), user.getId());
        
        // Verify
        assertThat(exists).isTrue();
        
        // Create another user with no reviews
        User anotherUser = new User();
        anotherUser.setUsername("another");
        anotherUser.setEmail("another@example.com");
        anotherUser.setPasswordHash("password456hash");
        anotherUser.setFirstName("Another");
        anotherUser.setLastName("User");
        anotherUser.setRole(Role.STUDENT);
        entityManager.persist(anotherUser);
        entityManager.flush();
        
        // Check if review exists for another user
        boolean notExists = reviewRepository.existsByContentIdAndUserId(content.getId(), anotherUser.getId());
        
        // Verify
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should delete reviews by content ID")
    public void shouldDeleteReviewsByContentId() {
        // Verify we have reviews for the content
        List<Review> reviewsBeforeDelete = reviewRepository.findByContentId(content.getId());
        assertThat(reviewsBeforeDelete).isNotEmpty();
        
        // Delete reviews by content ID
        reviewRepository.deleteByContentId(content.getId());
        entityManager.flush();
        entityManager.clear();
        
        // Verify reviews are deleted
        List<Review> reviewsAfterDelete = reviewRepository.findByContentId(content.getId());
        assertThat(reviewsAfterDelete).isEmpty();
    }
    
    @Test
    @DisplayName("Should update a review")
    public void shouldUpdateReview() {
        // Find the existing review
        Review existingReview = reviewRepository.findByContentId(content.getId()).get(0);
        
        // Update properties
        existingReview.setStars(2);
        existingReview.setSubject("Updated subject");
        existingReview.setComment("I changed my mind, it wasn't that helpful.");
        
        // Save the updated review
        reviewRepository.save(existingReview);
        entityManager.flush();
        entityManager.clear();
        
        // Retrieve the review again
        Review updatedReview = reviewRepository.findById(existingReview.getId()).orElse(null);
        
        // Verify
        assertThat(updatedReview).isNotNull();
        assertThat(updatedReview.getStars()).isEqualTo(2);
        assertThat(updatedReview.getSubject()).isEqualTo("Updated subject");
        assertThat(updatedReview.getComment()).isEqualTo("I changed my mind, it wasn't that helpful.");
    }
}