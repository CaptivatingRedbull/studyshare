package de.studyshare.studyshare.controller;

import java.util.List;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import de.studyshare.studyshare.domain.Lecturer;
import de.studyshare.studyshare.domain.LecturerDTO;
import de.studyshare.studyshare.domain.CourseDTO;
import de.studyshare.studyshare.service.LecturerService;

@RestController
@RequestMapping("/lecturers")
public class LecturerController {

    private final LecturerService lecturerService;

    public LecturerController(LecturerService lecturerService) {
        this.lecturerService = lecturerService;
    }

    @GetMapping
    public List<LecturerDTO> getLecturers() {
        return lecturerService.findAll();
    }

    @GetMapping("/{id}")
    public LecturerDTO getLecturerById(@PathVariable Long id) {
        return lecturerService.findOne(id);
    }

    @GetMapping("/{id}/courses")
    public List<CourseDTO> getLecturerCourses(@PathVariable Long id) {
        return lecturerService.getLecturerCoursesById(id);
    }

    @PostMapping
    public ResponseEntity<Lecturer> createLecturer(@RequestBody Lecturer lecturer) {
        return lecturerService.createLecturer(lecturer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLecturer(@PathVariable Long id) {
        return lecturerService.deleteLecturer(id);
    }

    @PostMapping("/{id}/courses/{courseId}")
    public ResponseEntity<LecturerDTO> addCourse(@PathVariable Long id, @PathVariable Long courseId) {
        return lecturerService.addCourseToLecturer(id, courseId);
    }

    @DeleteMapping("/{id}/courses/{courseId}")
    public ResponseEntity<?> removeCourse(@PathVariable Long id, @PathVariable Long courseId) {
        return lecturerService.removeCourseFromLecturer(id, courseId);
    }

    @PutMapping("/{id}/courses")
    public ResponseEntity<LecturerDTO> updateLecturerCourses(@PathVariable Long id, @RequestBody Set<Long> courseIds) {
        return lecturerService.updateLecturerCourses(id, courseIds);
    }
}
