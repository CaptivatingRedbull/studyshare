package de.studyshare.studyshare.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import de.studyshare.studyshare.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByUsername(String username);
}
