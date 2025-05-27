package de.studyshare.studyshare.domain;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Entity class representing a faculty in an educational institution.
 * 
 * A faculty serves as an organizational unit that contains multiple courses and
 * is associated with educational content shared on the platform.
 */
@Entity
public class Faculty {

    /**
     * Unique identifier for the faculty.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the faculty (e.g., "Computer Science", "Business Administration").
     */
    private String name;

    /**
     * Default constructor required by JPA.
     */
    public Faculty() {
    }

    /**
     * Constructs a new Faculty with the specified name.
     * 
     * @param name The name of the faculty
     */
    public Faculty(String name) {
        this.name = name;
    }

    /**
     * Constructs a new Faculty with the specified ID and name.
     * Primarily used for testing and data migration.
     * 
     * @param id   The unique identifier for the faculty
     * @param name The name of the faculty
     */
    public Faculty(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * @return The unique identifier of this faculty
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
     * @return The name of this faculty
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
     * Compares this faculty object with another object for equality.
     * Two faculty objects are considered equal if they have the same ID.
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
        Faculty faculty = (Faculty) o;
        return id != null ? id.equals(faculty.id) : faculty.id == null;
    }

    /**
     * Generates a hash code for this faculty object based on its ID.
     * 
     * @return The hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}