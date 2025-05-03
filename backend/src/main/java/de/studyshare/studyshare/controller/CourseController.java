package de.studyshare.studyshare.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.CourseDTO;
import de.studyshare.studyshare.domain.FacultyDTO;
import de.studyshare.studyshare.service.CourseService;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<CourseDTO> getCourses() {
        return courseService.getCourses();
    }

    @GetMapping("/{id}")
    public CourseDTO getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    @GetMapping("/{id}/faculty")
    public FacultyDTO getFaculty(@PathVariable Long id) {
        return courseService.getCourseFacultyById(id);
    }

    @PutMapping("/{id}/faculty/{facultyId}")
    public ResponseEntity<Course> updateCourseFaculty(@PathVariable Long id, @PathVariable Long facultyId) {
        return courseService.updateCourseFaculty(id, facultyId);
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        return courseService.createCourse(course);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        return courseService.deleteCourse(id);
    }

}
