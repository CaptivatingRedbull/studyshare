package de.studyshare.studyshare.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import de.studyshare.studyshare.domain.Lecturer;

public interface LecturerRepository extends JpaRepository<Lecturer, Long> {

    boolean existsByEmail(String email);

    Optional<Lecturer> findByEmail(String email);
}
