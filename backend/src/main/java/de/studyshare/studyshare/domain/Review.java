package de.studyshare.studyshare.domain;

import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * Entity class representing a user review for educational content.
 * 
 * Reviews include ratings (stars), feedback, and timestamps for creation and
 * updates.
 * Each review is associated with a specific content item and the user who
 * created it.
 */
@Entity
public class Review {

    /**
     * Unique identifier for the review.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Star rating for the content, constrained between 1 and 5.
     */
    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private int stars;

    /**
     * Brief subject or title of the review.
     */
    @Column(nullable = false, length = 100)
    private String subject;

    /**
     * Detailed comment or feedback about the content.
     * Stored as a LOB (Large Object) with a maximum length of 2000 characters.
     */
    @Lob
    @Column(length = 2000)
    private String comment;

    /**
     * Reference to the user who created this review.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Reference to the content being reviewed.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    /**
     * Timestamp when the review was created.
     * Automatically set by Hibernate.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the review was last updated.
     * Automatically updated by Hibernate.
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Default constructor required by JPA.
     */
    public Review() {
    }

    /**
     * Constructs a new Review with the specified attributes.
     * 
     * @param stars   Star rating (1-5)
     * @param subject Brief subject or title
     * @param comment Detailed feedback
     * @param user    User who created the review
     * @param content Content being reviewed
     */
    public Review(int stars, String subject, String comment, User user, Content content) {
        this.stars = stars;
        this.subject = subject;
        this.comment = comment;
        this.user = user;
        this.content = content;
    }

    /**
     * @return The unique identifier of this review
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id The unique identifier to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return The star rating (1-5)
     */
    public int getStars() {
        return stars;
    }

    /**
     * @param stars The star rating to set (1-5)
     */
    public void setStars(int stars) {
        this.stars = stars;
    }

    /**
     * @return The subject or title of the review
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject The subject or title to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return The detailed comment or feedback
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment The detailed comment or feedback to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return The user who created this review
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user The user to associate with this review
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return The content being reviewed
     */
    public Content getContent() {
        return content;
    }

    /**
     * @param content The content to associate with this review
     */
    public void setContent(Content content) {
        this.content = content;
    }

    /**
     * @return The timestamp when this review was created
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt The creation timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return The timestamp when this review was last updated
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt The update timestamp to set
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Compares this review object with another object for equality.
     * Two review objects are considered equal if they have the same ID.
     * 
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Review review = (Review) o;
        return id != null ? id.equals(review.id) : review.id == null;
    }

    /**
     * Generates a hash code for this review object based on its ID.
     * 
     * @return The hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}