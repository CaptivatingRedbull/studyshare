package de.studyshare.studyshare.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.CourseDTO;
import de.studyshare.studyshare.domain.Faculty;
import de.studyshare.studyshare.domain.FacultyDTO;
import de.studyshare.studyshare.domain.Lecturer;
import de.studyshare.studyshare.repository.CourseRepository;
import de.studyshare.studyshare.repository.FacultyRepository;
import de.studyshare.studyshare.repository.LecturerRepository;
import jakarta.transaction.Transactional;

@Service
public class CourseService {

    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;
    private final LecturerRepository lecturerRepository;

    private final Function<Course, CourseDTO> courseToCourseDTO = course -> new CourseDTO(
            course.getId(),
            course.getName(),
            course.getFaculty() != null
            ? new FacultyDTO(course.getFaculty().getId(), course.getFaculty().getName()) : null,
            course.getLecturers() != null ? course.getLecturers().stream().map(Lecturer::getId).collect(Collectors.toSet()) : Collections.emptySet());

    public CourseService(FacultyRepository facultyRepository,
            CourseRepository courseRepository,
            LecturerRepository lecturerRepository) {
        this.facultyRepository = facultyRepository;
        this.courseRepository = courseRepository;
        this.lecturerRepository = lecturerRepository;
    }

    @Transactional
    public List<CourseDTO> getCourses() {
        return courseRepository.findAll().stream().map(courseToCourseDTO).toList();
    }

    @Transactional
    public CourseDTO getCourseById(Long id) {
        return courseRepository.findById(id).map(courseToCourseDTO).orElse(null);
    }

    @Transactional
    public FacultyDTO getCourseFacultyById(Long id) {
        return courseRepository.findById(id)
                .map(course -> course.getFaculty() != null ? new FacultyDTO(course.getFaculty().getId(), course.getFaculty().getName()) : null)
                .orElse(null);
    }

    @Transactional
    public ResponseEntity<Course> updateCourseFaculty(Long id, Long facultyId) {
        Optional<Course> courseOptional = courseRepository.findById(id);
        Optional<Faculty> facultyOptional = facultyRepository.findById(facultyId);

        if (courseOptional.isPresent() && facultyOptional.isPresent()) {
            Course course = courseOptional.get();
            course.setFaculty(facultyOptional.get());
            courseRepository.save(course);
            return ResponseEntity.ok(course);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Transactional
    public ResponseEntity<CourseDTO> updateCourseLecturers(Long courseId, Set<Long> lecturerIds) {
        return courseRepository.findById(courseId).map(course -> {
            var lectors = lecturerRepository.findAllById(lecturerIds);
            course.setLecturers(new HashSet<>(lectors));
            var dto = courseToCourseDTO.apply(courseRepository.save(course));
            return ResponseEntity.ok(dto);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<Course> createCourse(Course course) {
        Course savedCourse = courseRepository.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
    }

    public ResponseEntity<?> deleteCourse(Long id) {
        return courseRepository.findById(id).map(course -> {
            courseRepository.delete(course);
            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
