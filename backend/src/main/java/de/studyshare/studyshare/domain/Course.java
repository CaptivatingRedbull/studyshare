package de.studyshare.studyshare.domain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

/**
 * Entity class representing a course in the educational system.
 * 
 * A course belongs to a faculty and can be taught by multiple lecturers.
 * It serves as an organizational unit for educational content shared in the
 * platform.
 */
@Entity
public class Course {

    /**
     * Unique identifier for the course.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the course.
     */
    private String name;

    /**
     * Faculty to which this course belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facultyId")
    private Faculty faculty;

    /**
     * Set of lecturers teaching this course.
     * Many-to-many relationship as multiple lecturers can teach the same course,
     * and a single lecturer can teach multiple courses.
     */
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "course_lecturer", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "lecturer_id"))
    private Set<Lecturer> lecturers = new HashSet<>();

    /**
     * Default constructor required by JPA.
     */
    public Course() {
    }

    /**
     * Constructs a new Course with specified name and faculty.
     * 
     * @param name    The name of the course
     * @param faculty The faculty to which the course belongs
     */
    public Course(String name, Faculty faculty) {
        this.name = name;
        this.faculty = faculty;
    }

    /**
     * @return The unique identifier of this course
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
     * @return The name of this course
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The faculty to which this course belongs
     */
    public Faculty getFaculty() {
        return faculty;
    }

    /**
     * @param faculty The faculty to associate with this course
     */
    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    /**
     * @return The set of lecturers teaching this course
     */
    public Set<Lecturer> getLecturers() {
        return lecturers;
    }

    /**
     * @param lecturers The set of lecturers to associate with this course
     */
    public void setLecturers(Set<Lecturer> lecturers) {
        this.lecturers = lecturers;
    }

    /**
     * Adds a lecturer to this course and updates the bidirectional relationship.
     * 
     * @param lecturer The lecturer to add to this course
     */
    public void addLecturer(Lecturer lecturer) {
        this.lecturers.add(lecturer);
        lecturer.getCourses().add(this);
    }

    /**
     * Removes a lecturer from this course and updates the bidirectional
     * relationship.
     * 
     * @param lecturer The lecturer to remove from this course
     */
    public void removeLecturer(Lecturer lecturer) {
        this.lecturers.remove(lecturer);
        lecturer.getCourses().remove(this);
    }

    /**
     * Compares this course object with another object for equality.
     * Two course objects are considered equal if they have the same ID.
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
        Course course = (Course) o;
        return id != null ? id.equals(course.id) : course.id == null;
    }

    /**
     * Generates a hash code for this course object based on its ID.
     * 
     * @return The hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}