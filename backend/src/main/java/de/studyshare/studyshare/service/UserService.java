package de.studyshare.studyshare.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import de.studyshare.studyshare.domain.User;
import de.studyshare.studyshare.domain.UserDTO;
import de.studyshare.studyshare.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public List<UserDTO> getUsers() {
        return userRepository.findAll().stream()
                .map(User::toDto)
                .toList();
    }

    @Transactional
    public List<UserDTO> getUserByUserName(String userName) {
        return userRepository.findByUserName(userName).stream()
                .map(User::toDto)
                .toList();
    }

    public ResponseEntity<User> createUser(User user) {
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    public ResponseEntity<?> deleteUser(Long id) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
