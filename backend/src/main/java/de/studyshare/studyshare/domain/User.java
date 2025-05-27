package de.studyshare.studyshare.domain;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entity class representing a user in the system.
 * 
 * This class stores user authentication details and personal information.
 * Username and email are enforced to be unique through database constraints.
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
})
public class User {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User's first name.
     * Cannot be blank and must be between 2 and 50 characters.
     */
    @NotBlank(message = "First name cannot be blank")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    /**
     * User's last name.
     * Cannot be blank and must be between 2 and 50 characters.
     */
    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    /**
     * User's email address.
     * Must be a valid email format and unique in the system.
     */
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;

    /**
     * Username for login purposes.
     * Must be unique, between 3 and 30 characters, and cannot be blank.
     */
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;

    /**
     * Hashed password for the user.
     * Raw passwords are never stored in the database.
     */
    @NotBlank(message = "Password hash cannot be blank")
    private String passwordHash;

    /**
     * User's role in the system (e.g., USER, ADMIN).
     * Stored as a string in the database using enum name.
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Role role;

    /**
     * Default constructor required by JPA.
     */
    public User() {
    }

    /**
     * Constructs a new User with all required fields.
     * 
     * @param firstName    User's first name
     * @param lastName     User's last name
     * @param email        User's email address
     * @param username     Username for login purposes
     * @param passwordHash Hashed password (never store raw passwords)
     * @param role         User's role in the system
     */
    public User(String firstName, String lastName, String email, String username, String passwordHash, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    /**
     * @return The unique identifier of this user
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id The unique identifier to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return The user's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName The first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return The user's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName The last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return The user's full name (first name + last name)
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * @return The user's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return The username used for login
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username The username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return The hashed password
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * @param passwordHash The hashed password to set
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * @return The user's role in the system
     */
    public Role getRole() {
        return role;
    }

    /**
     * @param role The role to set
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Compares this user object with another object for equality.
     * Two user objects are considered equal if they have the same ID.
     * 
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return id != null ? id.equals(user.id) : user.id == null;
    }

    /**
     * Generates a hash code for this user object based on its ID.
     * 
     * @return The hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}