package de.studyshare.studyshare.service;

import java.util.List;
import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import de.studyshare.studyshare.domain.User;
import de.studyshare.studyshare.domain.UserDTO;
import de.studyshare.studyshare.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final Function<User, UserDTO> userToUserDTO = user -> new UserDTO(
        user.getId(),
        user.getFirstName(),
        user.getLastName(),
        user.getEmail(),
        user.getPasswortHash(),
        user.getRole(),
        user.getUserName()
    );

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getUsers() {
        return userRepository.findAll().stream()
                .map(userToUserDTO)
                .toList();
    }

    public List<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username).stream()
                .map(userToUserDTO)
                .toList();
    }

    public ResponseEntity<User> createUser(User user) {
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    public ResponseEntity<?> deleteUser(long id) {
        return userRepository.findById(id).map(user -> {
            userRepository.delete(user);
            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
