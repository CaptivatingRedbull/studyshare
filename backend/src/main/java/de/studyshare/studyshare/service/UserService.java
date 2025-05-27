package de.studyshare.studyshare.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy; // Import @Lazy
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import de.studyshare.studyshare.domain.User;
import de.studyshare.studyshare.dto.entity.UserDTO;
import de.studyshare.studyshare.dto.request.UserCreateRequest;
import de.studyshare.studyshare.dto.request.UserUpdateRequest;
import de.studyshare.studyshare.exception.DuplicateResourceException;
import de.studyshare.studyshare.exception.ResourceNotFoundException;
import de.studyshare.studyshare.repository.UserRepository;
import jakarta.transaction.Transactional;

/**
 * Service class for managing users.
 * Provides methods to perform CRUD operations on users and handle user-related
 * business logic.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a UserService with the specified UserRepository and
     * PasswordEncoder.
     *
     * @param userRepository  the repository for managing users
     * @param passwordEncoder the password encoder for encoding passwords
     */
    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves all users from the repository.
     *
     * @return a list of UserDTO objects representing all users
     */
    @Transactional
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username of the user to retrieve
     * @return a UserDTO object representing the user
     * @throws ResourceNotFoundException if no user with the given username exists
     */
    @Transactional
    public UserDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(UserDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return a UserDTO object representing the user
     * @throws ResourceNotFoundException if no user with the given ID exists
     */
    @Transactional
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    /**
     * Creates a new user with the provided details.
     *
     * @param userCreateRequest the request containing the details of the user to
     *                          create
     * @return a UserDTO object representing the created user
     * @throws DuplicateResourceException if a user with the same username or email
     *                                    already exists
     */
    @Transactional
    public UserDTO createUser(UserCreateRequest userCreateRequest) {
        if (userRepository.existsByUsername(userCreateRequest.username())) {
            throw new DuplicateResourceException("User", "username", userCreateRequest.username());
        }
        if (userRepository.existsByEmail(userCreateRequest.email())) {
            throw new DuplicateResourceException("User", "email", userCreateRequest.email());
        }

        User user = new User();
        user.setFirstName(userCreateRequest.firstName());
        user.setLastName(userCreateRequest.lastName());
        user.setEmail(userCreateRequest.email());
        user.setUsername(userCreateRequest.username());
        user.setPasswordHash(passwordEncoder.encode(userCreateRequest.password()));
        user.setRole(userCreateRequest.role());

        User savedUser = userRepository.save(user);
        return UserDTO.fromEntity(savedUser);
    }

    /**
     * Updates an existing user with the provided details.
     *
     * @param id                the ID of the user to update
     * @param userUpdateRequest the request containing the updated details of the
     *                          user
     * @return a UserDTO object representing the updated user
     * @throws ResourceNotFoundException  if no user with the given ID exists
     * @throws DuplicateResourceException if a user with the same email already
     *                                    exists
     */
    @Transactional
    public UserDTO updateUser(Long id, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (userUpdateRequest.firstName() != null) {
            user.setFirstName(userUpdateRequest.firstName());
        }
        if (userUpdateRequest.lastName() != null) {
            user.setLastName(userUpdateRequest.lastName());
        }
        if (userUpdateRequest.email() != null) {

            if (!user.getEmail().equals(userUpdateRequest.email())
                    && userRepository.existsByEmail(userUpdateRequest.email())) {
                throw new DuplicateResourceException("User", "email", userUpdateRequest.email());
            }
            user.setEmail(userUpdateRequest.email());
        }

        User updatedUser = userRepository.save(user);
        return UserDTO.fromEntity(updatedUser);
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id the ID of the user to delete
     * @throws ResourceNotFoundException if no user with the given ID exists
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Checks if a user with the given username exists.
     *
     * @param username the username to check
     * @return true if a user with the given username exists, false otherwise
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Checks if a user with the given email exists.
     *
     * @param email the email to check
     * @return true if a user with the given email exists, false otherwise
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Retrieves an internal user by their username.
     *
     * @param username the username of the user to retrieve
     * @return a User object representing the user
     * @throws ResourceNotFoundException if no user with the given username exists
     */
    public User getInternalUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }
}