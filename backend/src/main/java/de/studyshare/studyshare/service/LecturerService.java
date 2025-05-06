package de.studyshare.studyshare.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.CourseDTO;
import de.studyshare.studyshare.domain.Lecturer;
import de.studyshare.studyshare.domain.LecturerDTO;
import de.studyshare.studyshare.repository.CourseRepository;
import de.studyshare.studyshare.repository.LecturerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class LecturerService {

    private final LecturerRepository lecturerRepository;
    private final CourseRepository courseRepository;

    public LecturerService(LecturerRepository lecturerRepository, CourseRepository courseRepository) {
        this.lecturerRepository = lecturerRepository;
        this.courseRepository = courseRepository;
    }

    public List<LecturerDTO> findAll() {
        return lecturerRepository.findAll().stream().map(Lecturer::toDto)
                .collect(Collectors.toList());
    }

    public LecturerDTO findOne(Long id) {
        return lecturerRepository.findById(id)
                .map(Lecturer::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Lecturer not found with id " + id));
    }

    @Transactional
    public ResponseEntity<Lecturer> createLecturer(Lecturer lecturer) {
        Lecturer savedLecturer = lecturerRepository.save(lecturer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLecturer);
    }

    public ResponseEntity<?> deleteLecturer(Long id) {
        return lecturerRepository.findById(id).map(lecturer -> {
            lecturerRepository.delete(lecturer);
            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Transactional
    public ResponseEntity<LecturerDTO> addCourseToLecturer(Long lecturerId, Long courseId) {
        return lecturerRepository.findById(lecturerId).map(lecturer -> {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new EntityNotFoundException("Course not found"));
            lecturer.addCourse(course);
            Lecturer saved = lecturerRepository.save(lecturer);
            return ResponseEntity.ok(saved.toDto());
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Transactional
    public ResponseEntity<?> removeCourseFromLecturer(Long lecturerId, Long courseId) {
        return lecturerRepository.findById(lecturerId).map(lecturer -> {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new EntityNotFoundException("Course not found"));
            lecturer.removeCourse(course);
            lecturerRepository.save(lecturer);
            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Transactional
    public ResponseEntity<LecturerDTO> updateLecturerCourses(Long lecturerId, Set<Long> courseIds) {
        return lecturerRepository.findById(lecturerId).map(lecturer -> {
            var courses = courseRepository.findAllById(courseIds);
            lecturer.setCourses(new HashSet<>(courses));
            var dto = lecturerRepository.save(lecturer).toDto();
            return ResponseEntity.ok(dto);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public List<CourseDTO> getLecturerCoursesById(Long lecturerId) {
        return lecturerRepository.findById(lecturerId)
                .map(lecturer -> lecturer.getCourses().stream()
                .map(Course::toDto)
                .collect(Collectors.toList()))
                .orElseThrow(() -> new EntityNotFoundException("Lecturer not found with id " + lecturerId));
    }
}
