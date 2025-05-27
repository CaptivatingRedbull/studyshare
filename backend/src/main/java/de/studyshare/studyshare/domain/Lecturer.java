package de.studyshare.studyshare.domain;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

/**
 * Entity class representing a lecturer in the educational system.
 * 
 * A lecturer can teach multiple courses and is associated with educational content
 * shared on the platform. This entity stores basic lecturer information and maintains
 * relationships with courses they teach.
 */
@Entity
public class Lecturer {

    /**
     * Unique identifier for the lecturer.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Full name of the lecturer.
     */
    private String name;
    
    /**
     * Email address of the lecturer.
     */
    private String email;

    /**
     * Set of courses taught by this lecturer.
     * Many-to-many relationship as a lecturer can teach multiple courses,
     * and a course can be taught by multiple lecturers.
     */
    @ManyToMany(mappedBy = "lecturers")
    private Set<Course> courses = new HashSet<>();

    /**
     * Default constructor required by JPA.
     */
    public Lecturer() {
    }

    /**
     * Constructs a new Lecturer with the specified name and email.
     * 
     * @param name The name of the lecturer
     * @param email The email address of the lecturer
     */
    public Lecturer(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * @return The unique identifier of this lecturer
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
     * @return The name of this lecturer
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
     * @return The email address of this lecturer
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return The set of courses taught by this lecturer
     */
    public Set<Course> getCourses() {
        return courses;
    }

    /**
     * @param courses The set of courses to associate with this lecturer
     */
    public void setCourses(Set<Course> courses) {
        this.courses = courses;
    }

    /**
     * Adds a course to this lecturer's teaching load and updates the bidirectional relationship.
     * 
     * @param course The course to add to this lecturer's teaching load
     */
    public void addCourse(Course course) {
        this.courses.add(course);
        course.getLecturers().add(this);
    }

    /**
     * Removes a course from this lecturer's teaching load and updates the bidirectional relationship.
     * 
     * @param course The course to remove from this lecturer's teaching load
     */
    public void removeCourse(Course course) {
        this.courses.remove(course);
        course.getLecturers().remove(this);
    }

    /**
     * Compares this lecturer object with another object for equality.
     * Two lecturer objects are considered equal if they have the same ID.
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
        Lecturer lecturer = (Lecturer) o;
        return id != null ? id.equals(lecturer.id) : lecturer.id == null;
    }

    /**
     * Generates a hash code for this lecturer object based on its ID.
     * 
     * @return The hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}