package de.studyshare.studyshare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.Faculty;

public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByNameAndFaculty(String name, Faculty faculty);

    boolean existsByNameAndFacultyAndIdNot(String name, Faculty faculty, Long courseId);

    boolean existsByFacultyId(Long facultyId);
}
