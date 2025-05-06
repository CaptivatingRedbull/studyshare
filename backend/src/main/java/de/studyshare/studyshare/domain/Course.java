package de.studyshare.studyshare.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facultyId")
    private Faculty faculty;

    @ManyToMany
    @JoinTable(
            name = "courseLecturer",
            joinColumns = @JoinColumn(name = "courseId"),
            inverseJoinColumns = @JoinColumn(name = "lecturerId")
    )
    private Set<Lecturer> lecturers = new HashSet<>();

    public Course() {
    }

    public Course(String name, Faculty faculty) {
        this.name = name;
        this.faculty = faculty;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public Set<Lecturer> getLecturers() {
        return this.lecturers;
    }

    public void setLecturers(Set<Lecturer> lecturers) {
        this.lecturers = lecturers;
    }

    public void addLecturer(Lecturer lecturer) {
        this.lecturers.add(lecturer);
        lecturer.getCourses().add(this);
    }

    public void removeLecturer(Lecturer lecturer) {
        this.lecturers.remove(lecturer);
        lecturer.getCourses().remove(this);
    }

    public CourseDTO toDto() {
        return new CourseDTO(
                this.id,
                this.name,
                this.faculty.toDto(),
                this.lecturers == null
                        ? Collections.emptySet()
                        : this.lecturers.stream()
                                .map(Lecturer::getId)
                                .collect(Collectors.toSet())
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Course)) {
            return false;
        }
        Course course = (Course) o;
        return id != null && id.equals(course.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
