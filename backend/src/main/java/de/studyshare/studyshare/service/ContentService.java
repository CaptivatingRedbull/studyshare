package de.studyshare.studyshare.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import de.studyshare.studyshare.domain.Content;
import de.studyshare.studyshare.domain.ContentCategory;
import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.Faculty;
import de.studyshare.studyshare.domain.Lecturer;
import de.studyshare.studyshare.domain.SortByOptions;
import de.studyshare.studyshare.domain.User;
import de.studyshare.studyshare.dto.entity.ContentDTO;
import de.studyshare.studyshare.dto.request.ContentCreateRequest;
import de.studyshare.studyshare.dto.request.ContentUpdateRequest;
import de.studyshare.studyshare.exception.BadRequestException;
import de.studyshare.studyshare.exception.ResourceNotFoundException;
import de.studyshare.studyshare.repository.ContentRepository;
import de.studyshare.studyshare.repository.CourseRepository;
import de.studyshare.studyshare.repository.FacultyRepository;
import de.studyshare.studyshare.repository.LecturerRepository;
import de.studyshare.studyshare.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

@Service
public class ContentService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private Path rootLocation;

    private final ContentRepository contentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final FacultyRepository facultyRepository;
    private final LecturerRepository lecturerRepository;

    public ContentService(ContentRepository contentRepository,
            UserRepository userRepository,
            CourseRepository courseRepository,
            FacultyRepository facultyRepository,
            LecturerRepository lecturerRepository) {
        this.contentRepository = contentRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.facultyRepository = facultyRepository;
        this.lecturerRepository = lecturerRepository;
    }

    @PostConstruct // Initialize after dependency injection
    public void init() {
        try {
            rootLocation = Paths.get(uploadDir);
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    @Transactional
    public List<ContentDTO> getAllContents() {
        return contentRepository.findAll().stream()
                .map(ContentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public ContentDTO getContentById(Long id) {
        return contentRepository.findById(id)
                .map(ContentDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", id));
    }

    @Transactional
    public ContentDTO createContent(ContentCreateRequest createRequest, MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        User uploadedByUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username",
                        currentUsername + " (authenticated user not found)"));

        Course course = courseRepository.findById(createRequest.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", createRequest.courseId()));
        Faculty faculty = facultyRepository.findById(createRequest.facultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", createRequest.facultyId()));

        Lecturer lecturer = null;

        if (!course.getFaculty().getId().equals(faculty.getId())) {
            throw new BadRequestException("The specified course (ID: " + course.getId()
                    + ") does not belong to the specified faculty (ID: " + faculty.getId() + ").");
        }

        String originalFilenameRaw = file.getOriginalFilename();
        if (originalFilenameRaw == null) {
            throw new BadRequestException("Uploaded file must have a filename.");
        }
        String originalFilename = StringUtils.cleanPath(originalFilenameRaw);
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        try {
            if (file.isEmpty()) {
                throw new BadRequestException("Failed to store empty file.");
            }
            Path destinationFile = this.rootLocation.resolve(
                    Paths.get(uniqueFilename))
                    .normalize().toAbsolutePath();
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }

        Content content = new Content();
        content.setUploadedBy(uploadedByUser);
        content.setUploadDate(LocalDate.now());
        content.setContentCategory(createRequest.contentCategory());
        content.setCourse(course);
        content.setFaculty(faculty);
        content.setLecturer(lecturer);
        content.setTitle(createRequest.title());
        content.setFilePath(uniqueFilename);
        

        Content savedContent = contentRepository.save(content);
        return ContentDTO.fromEntity(savedContent);
    }

    @Transactional
    public ContentDTO updateContent(Long id, ContentUpdateRequest updateRequest) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", id));

        content.setContentCategory(updateRequest.contentCategory());

        content.setTitle(updateRequest.title());

        Course newCourse = courseRepository.findById(updateRequest.courseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", updateRequest.courseId()));
        content.setCourse(newCourse);

        Faculty newFaculty = facultyRepository.findById(updateRequest.facultyId())
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", updateRequest.facultyId()));
        content.setFaculty(newFaculty);
        Lecturer newLecturer = lecturerRepository.findById(updateRequest.lecturerId())
                .orElseThrow(() -> new ResourceNotFoundException("Lecturer", "id", updateRequest.lecturerId()));
        content.setLecturer(newLecturer);

        if (content.getCourse() != null
                && !content.getCourse().getFaculty().getId().equals(content.getFaculty().getId())) {
            throw new BadRequestException("The content's course (ID: " + content.getCourse().getId()
                    + ") must belong to the content's faculty (ID: " + content.getFaculty().getId() + ").");
        }
        if (content.getLecturer() != null && content.getCourse() != null
                && !content.getCourse().getLecturers().contains(content.getLecturer())) {
            throw new BadRequestException("The content's lecturer (ID: " + content.getLecturer().getId()
                    + ") must be associated with the content's course (ID: " + content.getCourse().getId() + ").");
        }

        Content updatedContent = contentRepository.save(content);
        return ContentDTO.fromEntity(updatedContent);
    }

    @Transactional
    public void deleteContent(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", id));

        contentRepository.delete(content);

    }

    @Transactional
    public ContentDTO incrementReportCount(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", id));
        content.setReportedCount(content.getReportedCount() + 1);

        return ContentDTO.fromEntity(contentRepository.save(content));
    }

    @Transactional
    public ContentDTO incrementOutdatedCount(Long id) {
        Content content = contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content", "id", id));
        content.setOutdatedCount(content.getOutdatedCount() + 1);

        return ContentDTO.fromEntity(contentRepository.save(content));
    }

    @Transactional
    public List<ContentDTO> getContentsByFacultyId(Long facultyId) {
        return contentRepository.findByFacultyId(facultyId).stream()
                .map(ContentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ContentDTO> getContentsByCourseId(Long courseId) {
        return contentRepository.findByCourseId(courseId).stream()
                .map(ContentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public Page<ContentDTO> getFilteredAndSortedContents(
            Long facultyId,
            Long courseId,
            Long lecturerId,
            ContentCategory category,
            String searchTerm,
            SortByOptions sortBy,
            String sortDirection,
            Pageable pageable) {

        Specification<Content> spec = ContentSpecifications.filterBy(
                facultyId, courseId, lecturerId, category, searchTerm);

        Sort.Direction direction = (sortDirection != null && sortDirection.equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, sortBy != null ? sortBy.toString() : "uploadDate");

        PageRequest pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort);

        Page<Content> contentPage = contentRepository.findAll(spec, pageRequest);
        return contentPage.map(ContentDTO::fromEntity);
    }

    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = this.rootLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found " + filename +": " + ex);
        }
    }
}
