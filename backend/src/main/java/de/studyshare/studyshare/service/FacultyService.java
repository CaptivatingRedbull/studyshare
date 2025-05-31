package de.studyshare.studyshare.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import de.studyshare.studyshare.domain.Faculty;
import de.studyshare.studyshare.dto.entity.FacultyDTO;
import de.studyshare.studyshare.dto.request.FacultyCreateRequest;
import de.studyshare.studyshare.dto.request.FacultyUpdateRequest;
import de.studyshare.studyshare.exception.DuplicateResourceException;
import de.studyshare.studyshare.exception.ResourceNotFoundException;
import de.studyshare.studyshare.repository.CourseRepository;
import de.studyshare.studyshare.repository.FacultyRepository;
import jakarta.transaction.Transactional;

/**
 * Service class for managing faculties.
 * Provides methods to perform CRUD operations on faculties and their associated
 * courses.
 */
@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final CourseRepository courseRepository;
    private final CourseService courseService;

    /**
     * Constructs a FacultyService with the specified repositories.
     *
     * @param facultyRepository the repository for managing faculties
     * @param courseRepository  the repository for managing courses
     * @param courseService     the service for managing courses
     */
    public FacultyService(FacultyRepository facultyRepository, CourseRepository courseRepository,
            CourseService courseService) {
        this.facultyRepository = facultyRepository;
        this.courseRepository = courseRepository;
        this.courseService = courseService;
    }

    /**
     * Retrieves all faculties from the repository.
     *
     * @return a list of FacultyDTO objects representing all faculties
     */
    @Transactional
    public List<FacultyDTO> getAllFaculties() {
        return facultyRepository.findAll()
                .stream()
                .map(FacultyDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a faculty by its ID.
     *
     * @param id the ID of the faculty to retrieve
     * @return a FacultyDTO object representing the faculty with the specified ID
     * @throws ResourceNotFoundException if no faculty with the specified ID exists
     */
    @Transactional
    public FacultyDTO getFacultyById(Long id) {
        return facultyRepository.findById(id)
                .map(FacultyDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", id));
    }

    /**
     * Creates a new faculty with the specified name.
     *
     * @param createRequest the request containing the name of the faculty to create
     * @return a FacultyDTO object representing the created faculty
     * @throws DuplicateResourceException if a faculty with the same name already
     *                                    exists
     */
    @Transactional
    public FacultyDTO createFaculty(FacultyCreateRequest createRequest) {
        if (facultyRepository.existsByName(createRequest.name())) {
            throw new DuplicateResourceException("Faculty", "name", createRequest.name());
        }
        Faculty faculty = new Faculty(createRequest.name());
        Faculty savedFaculty = facultyRepository.save(faculty);
        return FacultyDTO.fromEntity(savedFaculty);
    }

    /**
     * Updates an existing faculty with the specified ID.
     *
     * @param id            the ID of the faculty to update
     * @param updateRequest the request containing the new name for the faculty
     * @return a FacultyDTO object representing the updated faculty
     * @throws ResourceNotFoundException  if no faculty with the specified ID exists
     * @throws DuplicateResourceException if a faculty with the same name already
     *                                    exists
     */
    @Transactional
    public FacultyDTO updateFaculty(Long id, FacultyUpdateRequest updateRequest) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", id));

        if (!faculty.getName().equalsIgnoreCase(updateRequest.name())
                && facultyRepository.existsByName(updateRequest.name())) {
            throw new DuplicateResourceException("Faculty", "name", updateRequest.name());
        }
        faculty.setName(updateRequest.name());
        Faculty updatedFaculty = facultyRepository.save(faculty);
        return FacultyDTO.fromEntity(updatedFaculty);
    }

    /**
     * Deletes a faculty by its ID.
     *
     * @param id the ID of the faculty to delete
     * @throws ResourceNotFoundException if no faculty with the specified ID exists
     */
    @Transactional
    public void deleteFaculty(long id) {
        Faculty faculty = facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", id));

        if (courseRepository.existsByFacultyId(id)) {
            courseRepository.findAllByFacultyId(id)
                    .forEach(course -> courseService.deleteCourse(course.getId()));
        }
        facultyRepository.delete(faculty);
    }
}
