package de.studyshare.studyshare.domain;

import java.time.LocalDate;

import jakarta.persistence.Entity;
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
    @JoinColumn(name = "userId")
    private User uploadedBy;

    private int reportedCount;
    private int outdatedCount;
    private LocalDate uploadDate;
    private ContentCategory contentCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecturerId")
    private Lecturer lecturer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "courseId")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facultyId")
    private Faculty faculty;

    private String filePath;

    public ContentDTO toDto() {
        return new ContentDTO(
                this.id,
                this.uploadedBy.toDto(),
                this.reportedCount,
                this.outdatedCount,
                this.uploadDate,
                this.contentCategory,
                this.lecturer.toDto(),
                this.course.toDto(),
                this.faculty.toDto(),
                this.filePath);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Lecturer getLecturer() {
        return lecturer;
    }

    public void setLecturer(Lecturer lecturer) {
        this.lecturer = lecturer;
    }

    public ContentCategory getCategory() {
        return contentCategory;
    }

    public void setCategory(ContentCategory contentCategory) {
        this.contentCategory = contentCategory;
    }

    public LocalDate getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDate uploadDate) {
        this.uploadDate = uploadDate;
    }

    public int getOutdatedCount() {
        return outdatedCount;
    }

    public void setOutdatedCount(int outdatedCount) {
        this.outdatedCount = outdatedCount;
    }

    public int getReportedCount() {
        return reportedCount;
    }

    public void setReportedCount(int reportedCount) {
        this.reportedCount = reportedCount;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
