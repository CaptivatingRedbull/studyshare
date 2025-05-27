package de.studyshare.studyshare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import de.studyshare.studyshare.domain.Content;

/**
 * Repository interface for managing Content entities.
 * Provides methods to perform CRUD operations and custom queries on Content
 * data.
 */
public interface ContentRepository extends JpaRepository<Content, Long>, JpaSpecificationExecutor<Content> {
    /**
     * Finds all Content entities associated with a specific faculty ID.
     *
     * @param facultyId the ID of the faculty to filter content by
     * @return a list of Content entities associated with the specified faculty ID
     */
    List<Content> findByFacultyId(Long facultyId);

    /**
     * Finds all Content entities associated with a specific course ID.
     *
     * @param courseId the ID of the course to filter content by
     * @return a list of Content entities associated with the specified course ID
     */
    List<Content> findByCourseId(Long courseId);
}
