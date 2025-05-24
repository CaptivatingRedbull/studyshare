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

@Entity
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User uploadedBy;

    private int reportedCount = 0;
    private int outdatedCount = 0;
    private LocalDate uploadDate;

    @Enumerated(EnumType.STRING)
    private ContentCategory contentCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturer_id")
    private Lecturer lecturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    private String filePath;
    private String title;

    @Column(name = "average_rating") 
    private Double averageRating = 0.0; 

    public Content() {
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public int getReportedCount() {
        return reportedCount;
    }

    public void setReportedCount(int reportedCount) {
        this.reportedCount = reportedCount;
    }

    public int getOutdatedCount() {
        return outdatedCount;
    }

    public void setOutdatedCount(int outdatedCount) {
        this.outdatedCount = outdatedCount;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }

    public ContentCategory getContentCategory() {
        return contentCategory;
    } // Getter for contentCategory

    public void setContentCategory(ContentCategory contentCategory) {
        this.contentCategory = contentCategory;
    }

    public Lecturer getLecturer() {
        return lecturer;
    }

    public void setLecturer(Lecturer lecturer) {
        this.lecturer = lecturer;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
