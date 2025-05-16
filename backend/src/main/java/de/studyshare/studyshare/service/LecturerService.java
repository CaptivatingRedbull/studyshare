package de.studyshare.studyshare.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.Lecturer;
import de.studyshare.studyshare.dto.entity.LecturerDTO;
import de.studyshare.studyshare.dto.request.LecturerCreateRequest;
import de.studyshare.studyshare.dto.request.LecturerUpdateRequest;
import de.studyshare.studyshare.exception.DuplicateResourceException;
import de.studyshare.studyshare.exception.ResourceNotFoundException;
import de.studyshare.studyshare.repository.CourseRepository;
import de.studyshare.studyshare.repository.LecturerRepository;
import jakarta.transaction.Transactional;

@Service
public class LecturerService {

    private final LecturerRepository lecturerRepository;
    private final CourseRepository courseRepository;

    public LecturerService(LecturerRepository lecturerRepository, CourseRepository courseRepository) {
        this.lecturerRepository = lecturerRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public List<LecturerDTO> getAllLecturers() {
        return lecturerRepository.findAll().stream()
                .map(LecturerDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public LecturerDTO getLecturerById(Long id) {
        return lecturerRepository.findById(id)
                .map(LecturerDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer", "id", id));
    }

    @Transactional
    public LecturerDTO createLecturer(LecturerCreateRequest createRequest) {
        if (createRequest.email() != null && !createRequest.email().isEmpty() && lecturerRepository.existsByEmail(createRequest.email())) {
            throw new DuplicateResourceException("Lecturer", "email", createRequest.email());
        }

        Lecturer lecturer = new Lecturer();
        lecturer.setName(createRequest.name());
        lecturer.setEmail(createRequest.email());

        if (createRequest.courseIds() != null && !createRequest.courseIds().isEmpty()) {
            Set<Course> courses = new HashSet<>(courseRepository.findAllById(createRequest.courseIds()));

            if (courses.size() != createRequest.courseIds().size()) {

                throw new ResourceNotFoundException("One or more courses not found for the provided IDs.");
            }
            lecturer.setCourses(courses);
        }

        Lecturer savedLecturer = lecturerRepository.save(lecturer);
        return LecturerDTO.fromEntity(savedLecturer);
    }

    @Transactional
    public LecturerDTO updateLecturer(Long id, LecturerUpdateRequest updateRequest) {
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer", "id", id));

        if (updateRequest.name() != null) {
            lecturer.setName(updateRequest.name());
        }
        if (updateRequest.email() != null) {
            if (!lecturer.getEmail().equals(updateRequest.email()) && lecturerRepository.existsByEmail(updateRequest.email())) {
                throw new DuplicateResourceException("Lecturer", "email", updateRequest.email());
            }
            lecturer.setEmail(updateRequest.email());
        }

        if (updateRequest.courseIds() != null) {
            Set<Course> newCourses = new HashSet<>(courseRepository.findAllById(updateRequest.courseIds()));

            for (Course existingCourse : new HashSet<>(lecturer.getCourses())) {
                if (!newCourses.contains(existingCourse)) {
                    lecturer.removeCourse(existingCourse);
                }
            }

            for (Course newCourse : newCourses) {
                if (!lecturer.getCourses().contains(newCourse)) {
                    lecturer.addCourse(newCourse);
                }
            }
        }

        Lecturer updatedLecturer = lecturerRepository.save(lecturer);
        return LecturerDTO.fromEntity(updatedLecturer);
    }

    @Transactional
    public void deleteLecturer(Long id) {
        Lecturer lecturer = lecturerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer", "id", id));

        for (Course course : new HashSet<>(lecturer.getCourses())) {
            course.getLecturers().remove(lecturer);
        }
        lecturer.getCourses().clear();
        lecturerRepository.save(lecturer);

        lecturerRepository.delete(lecturer);
    }

}
