package de.studyshare.studyshare.domain;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Entity class representing educational content uploaded by users.
 * 
 * Content items can be categorized, associated with courses, lecturers, and
 * faculties,
 * and tracked for reporting, ratings, and outdated status.
 */
@Entity
public class Content {

    /**
     * Unique identifier for the content item.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Reference to the user who uploaded this content.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User uploadedBy;

    /**
     * Number of times this content has been reported by users.
     */
    private int reportedCount = 0;

    /**
     * Number of times this content has been marked as outdated by users.
     */
    private int outdatedCount = 0;

    /**
     * Date when the content was uploaded.
     */
    private LocalDate uploadDate;

    /**
     * Category of the content (e.g., LECTURE_NOTES, EXAM, ASSIGNMENT).
     */
    @Enumerated(EnumType.STRING)
    private ContentCategory contentCategory;

    /**
     * Lecturer associated with this content.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id")
    private Lecturer lecturer;

    /**
     * Course to which this content belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    /**
     * Faculty to which this content is related.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    /**
     * Path to the uploaded file in the storage system.
     */
    private String filePath;

    /**
     * Title or name of the content.
     */
    private String title;

    /**
     * Average rating of the content based on user reviews.
     */
    @Column(name = "average_rating")
    private Double averageRating = 0.0;

    /**
     * Default constructor required by JPA.
     */
    public Content() {
    }

    /**
     * Constructs a new Content instance with specified attributes.
     * 
     * @param title           Title of the content
     * @param filePath        Path to the stored file
     * @param contentCategory Category of the content
     * @param faculty         Associated faculty
     * @param course          Associated course
     * @param lecturer        Associated lecturer
     * @param uploadedBy      User who uploaded the content
     * @param uploadDate      Date when the content was uploaded
     * @param reportedCount   Initial number of reports (usually 0)
     * @param outdatedCount   Initial number of outdated marks (usually 0)
     */
    public Content(String title, String filePath, ContentCategory contentCategory, Faculty faculty, Course course,
            Lecturer lecturer, User uploadedBy, LocalDate uploadDate, int reportedCount, int outdatedCount) {
        this.uploadedBy = uploadedBy;
        this.reportedCount = reportedCount;
        this.outdatedCount = outdatedCount;
        this.uploadDate = uploadDate;
        this.contentCategory = contentCategory;
        this.lecturer = lecturer;
        this.course = course;
        this.faculty = faculty;
        this.filePath = filePath;
        this.title = title;
        this.averageRating = 0.0;
    }

    /**
     * @return The unique identifier of this content
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
     * @return The user who uploaded this content
     */
    public User getUploadedBy() {
        return uploadedBy;
    }

    /**
     * @param uploadedBy The user who uploaded this content
     */
    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    /**
     * @return The number of times this content has been reported
     */
    public int getReportedCount() {
        return reportedCount;
    }

    /**
     * @param reportedCount The number of reports to set
     */
    public void setReportedCount(int reportedCount) {
        this.reportedCount = reportedCount;
    }

    /**
     * @return The number of times this content has been marked as outdated
     */
    public int getOutdatedCount() {
        return outdatedCount;
    }

    /**
     * @param outdatedCount The number of outdated marks to set
     */
    public void setOutdatedCount(int outdatedCount) {
        this.outdatedCount = outdatedCount;
    }

    /**
     * @return The date when this content was uploaded
     */
    public LocalDate getUploadDate() {
        return uploadDate;
    }

    /**
     * @param uploadDate The upload date to set
     */
    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }

    /**
     * @return The category of this content
     */
    public ContentCategory getContentCategory() {
        return contentCategory;
    }

    /**
     * @param contentCategory The content category to set
     */
    public void setContentCategory(ContentCategory contentCategory) {
        this.contentCategory = contentCategory;
    }

    /**
     * @return The lecturer associated with this content
     */
    public Lecturer getLecturer() {
        return lecturer;
    }

    /**
     * @param lecturer The lecturer to associate with this content
     */
    public void setLecturer(Lecturer lecturer) {
        this.lecturer = lecturer;
    }

    /**
     * @return The course associated with this content
     */
    public Course getCourse() {
        return course;
    }

    /**
     * @param course The course to associate with this content
     */
    public void setCourse(Course course) {
        this.course = course;
    }

    /**
     * @return The faculty associated with this content
     */
    public Faculty getFaculty() {
        return faculty;
    }

    /**
     * @param faculty The faculty to associate with this content
     */
    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    /**
     * @return The file path where the content is stored
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @param filePath The file path to set
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @return The title of this content
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return The average rating of this content
     */
    public Double getAverageRating() {
        return averageRating;
    }

    /**
     * @param averageRating The average rating to set
     */
    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    /**
     * Compares this content object with another object for equality.
     * Two content objects are considered equal if they have the same ID.
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
        Content content = (Content) o;
        return id != null ? id.equals(content.id) : content.id == null;
    }

    /**
     * Generates a hash code for this content object based on its ID.
     * 
     * @return The hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}