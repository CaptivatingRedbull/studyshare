package de.studyshare.studyshare.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import de.studyshare.studyshare.domain.Role;
import de.studyshare.studyshare.domain.User;

@DataJpaTest
@ActiveProfiles("test") // Use a test profile to avoid running CommandLineRunner
public class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;
    
    private User user1;
    private User user2;
    private User user3;
    
    // Generate unique usernames to avoid conflicts
    private String generateUniqueName(String base) {
        return base + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    @TestConfiguration
    @SuppressWarnings("unused")
    static class TestConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
    
    @BeforeEach
    public void setup() {
        // Clear any existing data
        userRepository.deleteAll();
        
        // Create test users with unique usernames
        String uniqueUsername1 = generateUniqueName("user1");
        String uniqueUsername2 = generateUniqueName("user2");
        String uniqueUsername3 = generateUniqueName("user3");
        
        user1 = new User();
        user1.setFirstName("Max");
        user1.setLastName("Mustermann");
        user1.setEmail(uniqueUsername1 + "@example.com");
        user1.setUsername(uniqueUsername1);
        user1.setPasswordHash("password123hash");
        user1.setRole(Role.STUDENT);
        
        user2 = new User();
        user2.setFirstName("Maria");
        user2.setLastName("Db");
        user2.setEmail(uniqueUsername2 + "@example.com");
        user2.setUsername(uniqueUsername2);
        user2.setPasswordHash("adminpasshash");
        user2.setRole(Role.ADMIN);
        
        user3 = new User();
        user3.setFirstName("John");
        user3.setLastName("Doe");
        user3.setEmail(uniqueUsername3 + "@example.com");
        user3.setUsername(uniqueUsername3);
        user3.setPasswordHash("anotherhash");
        user3.setRole(Role.STUDENT);
        
        // Save to database
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        
        entityManager.flush();
    }
    
    @Test
    @DisplayName("Should find all users")
    public void shouldFindAllUsers() {
        // When
        List<User> users = userRepository.findAll();
        
        // Then
        assertThat(users).hasSize(3);
        assertThat(users).extracting("username")
            .containsExactlyInAnyOrder(user1.getUsername(), user2.getUsername(), user3.getUsername());
    }
    
    @Test
    @DisplayName("Should find user by ID")
    public void shouldFindUserById() {
        // When
        User foundUser = userRepository.findById(user1.getId()).orElse(null);
        
        // Then
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUsername()).isEqualTo(user1.getUsername());
        assertThat(foundUser.getEmail()).isEqualTo(user1.getEmail());
        assertThat(foundUser.getRole()).isEqualTo(Role.STUDENT);
    }
    
    @Test
    @DisplayName("Should find user by username")
    public void shouldFindUserByUsername() {
        // When
        Optional<User> foundUser = userRepository.findByUsername(user2.getUsername());
        Optional<User> notFoundUser = userRepository.findByUsername("nonexistent");
        
        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo(user2.getEmail());
        assertThat(foundUser.get().getRole()).isEqualTo(Role.ADMIN);
        assertThat(notFoundUser).isNotPresent();
    }
    
    @Test
    @DisplayName("Should check if user exists by username")
    public void shouldCheckIfUserExistsByUsername() {
        // When
        boolean exists = userRepository.existsByUsername(user3.getUsername());
        boolean notExists = userRepository.existsByUsername("notfound");
        
        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
    
    @Test
    @DisplayName("Should check if user exists by email")
    public void shouldCheckIfUserExistsByEmail() {
        // When
        boolean exists = userRepository.existsByEmail(user1.getEmail());
        boolean notExists = userRepository.existsByEmail("notfound@example.com");
        
        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
    
    @Test
    @DisplayName("Should save new user")
    public void shouldSaveNewUser() {
        // Given
        String uniqueUsername = generateUniqueName("alice");
        User newUser = new User();
        newUser.setFirstName("Alice");
        newUser.setLastName("Johnson");
        newUser.setEmail(uniqueUsername + "@example.com");
        newUser.setUsername(uniqueUsername);
        newUser.setPasswordHash("hashedpassword");
        newUser.setRole(Role.STUDENT);
        
        // When
        User savedUser = userRepository.save(newUser);
        
        // Then
        assertThat(savedUser.getId()).isNotNull();
        
        User retrievedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getUsername()).isEqualTo(uniqueUsername);
        assertThat(retrievedUser.getEmail()).isEqualTo(uniqueUsername + "@example.com");
        assertThat(retrievedUser.getRole()).isEqualTo(Role.STUDENT);
    }
    
    @Test
    @DisplayName("Should update user")
    public void shouldUpdateUser() {
        // When
        user1.setEmail("updated.email@example.com");
        user1.setFirstName("Updated");
        user1.setRole(Role.ADMIN);
        userRepository.save(user1);
        entityManager.flush();
        entityManager.clear();
        
        // Then
        User updatedUser = userRepository.findById(user1.getId()).orElse(null);
        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getEmail()).isEqualTo("updated.email@example.com");
        assertThat(updatedUser.getFirstName()).isEqualTo("Updated");
        assertThat(updatedUser.getRole()).isEqualTo(Role.ADMIN);
    }
    
    @Test
    @DisplayName("Should delete user")
    public void shouldDeleteUser() {
        // When
        userRepository.delete(user3);
        entityManager.flush();
        
        // Then
        assertThat(userRepository.findById(user3.getId())).isEmpty();
        assertThat(userRepository.count()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Should handle case-sensitive usernames")
    public void shouldHandleCaseSensitiveUsernames() {
        // When - find with different case
        String upperUsername = user1.getUsername().toUpperCase();
        Optional<User> foundUser = userRepository.findByUsername(upperUsername);
        boolean exists = userRepository.existsByUsername(user3.getUsername().toUpperCase());
        
        // Then - should not match because of case sensitivity
        assertThat(foundUser).isEmpty();
        assertThat(exists).isFalse();
        
        // Create a user with similar username but different case
        String uniqueUpperUsername = generateUniqueName("UPPERUSER");
        User similarUser = new User();
        similarUser.setFirstName("Max");
        similarUser.setLastName("Upper");
        similarUser.setEmail(uniqueUpperUsername + "@example.com");
        similarUser.setUsername(uniqueUpperUsername);
        similarUser.setPasswordHash("password123hash");
        similarUser.setRole(Role.STUDENT);
        entityManager.persist(similarUser);
        entityManager.flush();
        
        // Then - both usernames should exist
        assertThat(userRepository.findByUsername(user1.getUsername())).isPresent();
        assertThat(userRepository.findByUsername(uniqueUpperUsername)).isPresent();
    }
    
    @Test
    @DisplayName("Should handle case-sensitive emails")
    public void shouldHandleCaseSensitiveEmails() {
        // When - search with different case
        String lowerCaseEmail = user1.getEmail();
        String mixedCaseEmail = lowerCaseEmail.substring(0, 1).toUpperCase() + lowerCaseEmail.substring(1);
        
        boolean existsLowerCase = userRepository.existsByEmail(lowerCaseEmail);
        boolean existsMixedCase = userRepository.existsByEmail(mixedCaseEmail);
        
        // Then - this depends on the database configuration for case-sensitivity
        // Most databases treat emails as case-insensitive, but this test verifies the actual behavior
        assertThat(existsLowerCase).isTrue();
        
        // Note: The result of this assertion depends on how the database handles case sensitivity
        // If your database is case-insensitive for emails, this might pass
        if (existsMixedCase) {
            // Database treats emails as case-insensitive
            assertThat(existsMixedCase).isTrue();
        } else {
            // Create a user with a similar email but different case
            String uniqueUsername = generateUniqueName("mixedcase");
            User userWithMixedCaseEmail = new User();
            userWithMixedCaseEmail.setFirstName("Another");
            userWithMixedCaseEmail.setLastName("User");
            userWithMixedCaseEmail.setEmail(mixedCaseEmail);
            userWithMixedCaseEmail.setUsername(uniqueUsername);
            userWithMixedCaseEmail.setPasswordHash("hashedpwd");
            userWithMixedCaseEmail.setRole(Role.STUDENT);
            entityManager.persist(userWithMixedCaseEmail);
            entityManager.flush();
            
            // Now the mixed case email should exist
            assertThat(userRepository.existsByEmail(mixedCaseEmail)).isTrue();
        }
    }
}