package de.studyshare.studyshare.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.studyshare.studyshare.domain.Lecturer;

public interface LecturerRepository extends JpaRepository<Lecturer, Long> {
}
