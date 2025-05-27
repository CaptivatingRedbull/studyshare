package de.studyshare.studyshare.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.Faculty;
import de.studyshare.studyshare.domain.Lecturer;
import de.studyshare.studyshare.dto.entity.CourseDTO;
import de.studyshare.studyshare.dto.request.CourseCreateRequest;
import de.studyshare.studyshare.dto.request.CourseUpdateRequest;
import de.studyshare.studyshare.exception.BadRequestException;
import de.studyshare.studyshare.exception.DuplicateResourceException;
import de.studyshare.studyshare.exception.ResourceNotFoundException;
import de.studyshare.studyshare.repository.CourseRepository;
import de.studyshare.studyshare.repository.FacultyRepository;
import de.studyshare.studyshare.repository.LecturerRepository;
import jakarta.transaction.Transactional;

/**
 * Service class for managing courses in the study share application.
 * Provides methods to create, update, delete, and retrieve courses.
 */
@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;
    private final LecturerRepository lecturerRepository;

    /**
     * Constructs a CourseService with the specified repositories.
     *
     * @param courseRepository   the repository to access course data
     * @param facultyRepository  the repository to access faculty data
     * @param lecturerRepository the repository to access lecturer data
     */
    public CourseService(CourseRepository courseRepository,
            FacultyRepository facultyRepository,
            LecturerRepository lecturerRepository) {
        this.courseRepository = courseRepository;
        this.facultyRepository = facultyRepository;
        this.lecturerRepository = lecturerRepository;
    }

    /**
     * Retrieves all courses from the repository.
     *
     * @return a list of CourseDTO objects representing all courses
     */
    @Transactional
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(CourseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a course by its ID.
     *
     * @param id the ID of the course to retrieve
     * @return a CourseDTO object representing the course
     * @throws ResourceNotFoundException if the course with the specified ID does
     *                                   not exist
     */
    @Transactional
    public CourseDTO getCourseById(Long id) {
        return courseRepository.findById(id)
                .map(CourseDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

    /**
     * Creates a new course with the specified details.
     *
     * @param createRequest the request object containing course creation details
     * @return a CourseDTO object representing the created course
     * @throws ResourceNotFoundException  if the faculty with the specified ID does
     *                                    not exist
     * @throws DuplicateResourceException if a course with the same name already
     *                                    exists in the faculty
     */
    @Transactional
    public CourseDTO createCourse(CourseCreateRequest createRequest) {
        Faculty faculty = facultyRepository.findById(createRequest.facultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", createRequest.facultyId()));

        if (courseRepository.existsByNameAndFaculty(createRequest.name(), faculty)) {
            throw new DuplicateResourceException("Course with name '" + createRequest.name()
                    + "' already exists in faculty '" + faculty.getName() + "'.");
        }

        Course course = new Course();
        course.setName(createRequest.name());
        course.setFaculty(faculty);

        if (createRequest.lecturerIds() != null && !createRequest.lecturerIds().isEmpty()) {
            Set<Lecturer> lecturers = new HashSet<>(lecturerRepository.findAllById(createRequest.lecturerIds()));
            if (lecturers.size() != createRequest.lecturerIds().size()) {
                throw new ResourceNotFoundException("One or more lecturers not found for the provided IDs.");
            }

            lecturers.forEach(course::addLecturer);
        }

        Course savedCourse = courseRepository.save(course);
        return CourseDTO.fromEntity(savedCourse);
    }

    /**
     * Updates an existing course with the specified details.
     *
     * @param id            the ID of the course to update
     * @param updateRequest the request object containing course update details
     * @return a CourseDTO object representing the updated course
     * @throws ResourceNotFoundException  if the course or faculty with the
     *                                    specified IDs do not exist
     * @throws DuplicateResourceException if a course with the same name already
     *                                    exists in the faculty
     */
    @Transactional
    public CourseDTO updateCourse(Long id, CourseUpdateRequest updateRequest) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        if (updateRequest.name() != null) {

            Faculty facultyToCheck = (updateRequest.facultyId() != null)
                    ? facultyRepository.findById(updateRequest.facultyId())
                            .orElseThrow(
                                    () -> new ResourceNotFoundException("Faculty", "id", updateRequest.facultyId()))
                    : course.getFaculty();

            if (!course.getName().equals(updateRequest.name()) || (updateRequest.facultyId() != null
                    && !course.getFaculty().getId().equals(updateRequest.facultyId()))) {
                if (courseRepository.existsByNameAndFacultyAndIdNot(updateRequest.name(), facultyToCheck, id)) {
                    throw new DuplicateResourceException("Course with name '" + updateRequest.name()
                            + "' already exists in faculty '" + facultyToCheck.getName() + "'.");
                }
            }
            course.setName(updateRequest.name());
        }

        if (updateRequest.facultyId() != null && !course.getFaculty().getId().equals(updateRequest.facultyId())) {
            Faculty newFaculty = facultyRepository.findById(updateRequest.facultyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", updateRequest.facultyId()));
            course.setFaculty(newFaculty);
        }

        if (updateRequest.lecturerIds() != null) {
            Set<Lecturer> newLecturers = new HashSet<>();
            if (!updateRequest.lecturerIds().isEmpty()) {
                newLecturers.addAll(lecturerRepository.findAllById(updateRequest.lecturerIds()));
                if (newLecturers.size() != updateRequest.lecturerIds().size()) {
                    throw new ResourceNotFoundException(
                            "One or more lecturers not found for the provided IDs during update.");
                }
            }

            Set<Lecturer> currentLecturers = new HashSet<>(course.getLecturers());

            currentLecturers.stream()
                    .filter(lecturer -> !newLecturers.contains(lecturer))
                    .collect(Collectors.toSet())
                    .forEach(course::removeLecturer);

            newLecturers.stream()
                    .filter(lecturer -> !currentLecturers.contains(lecturer))
                    .forEach(course::addLecturer);
        }

        Course updatedCourse = courseRepository.save(course);
        return CourseDTO.fromEntity(updatedCourse);
    }

    /**
     * Deletes a course by its ID.
     *
     * @param id the ID of the course to delete
     * @throws ResourceNotFoundException if the course with the specified ID does
     *                                   not exist
     */
    @Transactional
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        new HashSet<>(course.getLecturers()).forEach(course::removeLecturer);

        courseRepository.delete(course);
    }

    /**
     * Adds a lecturer to a course.
     *
     * @param courseId   the ID of the course to which the lecturer will be added
     * @param lecturerId the ID of the lecturer to add
     * @return a CourseDTO object representing the updated course
     * @throws ResourceNotFoundException if the course or lecturer with the
     *                                   specified IDs do not exist
     * @throws BadRequestException       if the lecturer is already assigned to the
     *                                   course
     */
    @Transactional
    public CourseDTO addLecturerToCourse(Long courseId, Long lecturerId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));
        Lecturer lecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer", "id", lecturerId));

        if (course.getLecturers().contains(lecturer)) {
            throw new BadRequestException(
                    "Lecturer with id " + lecturerId + " is already assigned to course " + courseId);
        }
        course.addLecturer(lecturer);
        return CourseDTO.fromEntity(courseRepository.save(course));
    }

    /**
     * Removes a lecturer from a course.
     *
     * @param courseId   the ID of the course from which the lecturer will be
     *                   removed
     * @param lecturerId the ID of the lecturer to remove
     * @return a CourseDTO object representing the updated course
     * @throws ResourceNotFoundException if the course or lecturer with the
     *                                   specified IDs do not exist
     * @throws BadRequestException       if the lecturer is not assigned to the
     *                                   course
     */
    @Transactional
    public CourseDTO removeLecturerFromCourse(Long courseId, Long lecturerId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));
        Lecturer lecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer", "id", lecturerId));

        if (!course.getLecturers().contains(lecturer)) {
            throw new BadRequestException("Lecturer with id " + lecturerId + " is not assigned to course " + courseId);
        }
        course.removeLecturer(lecturer);
        lecturerRepository.save(lecturer);
        return CourseDTO.fromEntity(courseRepository.save(course));
    }
}
