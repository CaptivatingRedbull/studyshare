package de.studyshare.studyshare.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.studyshare.studyshare.domain.Faculty;

public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    boolean existsByName(String name);

    Optional<Faculty> findByName(String name);
}
