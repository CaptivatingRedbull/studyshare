package de.studyshare.studyshare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.studyshare.studyshare.domain.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {

}
