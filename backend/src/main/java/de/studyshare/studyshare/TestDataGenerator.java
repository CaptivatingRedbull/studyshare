package de.studyshare.studyshare;

import java.time.LocalDate;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.studyshare.studyshare.domain.Content;
import de.studyshare.studyshare.domain.ContentCategory;
import de.studyshare.studyshare.domain.Course;
import de.studyshare.studyshare.domain.Faculty;
import de.studyshare.studyshare.domain.Lecturer;
import de.studyshare.studyshare.domain.Role;
import de.studyshare.studyshare.domain.User;
import de.studyshare.studyshare.repository.ContentRepository;
import de.studyshare.studyshare.repository.CourseRepository;
import de.studyshare.studyshare.repository.FacultyRepository;
import de.studyshare.studyshare.repository.LecturerRepository;
import de.studyshare.studyshare.repository.UserRepository;

@Configuration
public class TestDataGenerator {

    @Bean
    public CommandLineRunner initializeTestData(
            UserRepository userRepository,
            FacultyRepository facultyRepository,
            CourseRepository courseRepository,
            LecturerRepository lecturerRepository,
            ContentRepository contentRepository,
            PasswordEncoder passwordEncoder) {
        
        return args -> {
            // Test data entities
            User testUser;
            User adminUser;
            Faculty facultyEng;
            Faculty facultyCS;
            Course courseAlgo;
            Course courseWebDev;
            Course courseMath;
            Lecturer lecturerSmith;
            Lecturer lecturerDoe;
            Content content1, content2, content3, content4, content5;

            User user1 = new User("Max", "Mustermann", "max.mustermann@hotmail.com",
                    "mmustermann", passwordEncoder.encode("password123"), Role.STUDENT);
            userRepository.save(user1);

            User user2 = new User("Maria", "Db", "maria.db@hotmail.com",
                    "mariadb", passwordEncoder.encode("adminpass"), Role.ADMIN);
            userRepository.save(user2);

            testUser = new User("Test", "User", "testuser@example.com", "testuser",
                    passwordEncoder.encode("password"), Role.STUDENT);
            userRepository.save(testUser);

            adminUser = new User("Admin", "User", "admin@example.com", "admin", passwordEncoder.encode("adminpass"),
                    Role.ADMIN);
            userRepository.save(adminUser);

            // Setup Faculties
            facultyEng = new Faculty("Engineering");
            facultyRepository.save(facultyEng);
            facultyCS = new Faculty("Computer Science");
            facultyRepository.save(facultyCS);

            // Setup Lecturers
            lecturerSmith = new Lecturer("Dr. Smith", "smith@uni.edu");
            lecturerRepository.save(lecturerSmith);
            lecturerDoe = new Lecturer("Prof. Doe", "doe@uni.edu");
            lecturerRepository.save(lecturerDoe);

            // Setup Courses
            courseAlgo = new Course("Algorithms", facultyCS);
            courseRepository.save(courseAlgo); // Save before adding lecturers
            courseWebDev = new Course("Web Development", facultyCS);
            courseRepository.save(courseWebDev);
            courseMath = new Course("Calculus", facultyEng);
            courseRepository.save(courseMath);

            // Associate lecturers with courses
            courseAlgo.addLecturer(lecturerDoe);
            courseWebDev.addLecturer(lecturerSmith);
            courseMath.addLecturer(lecturerSmith);
            courseRepository.saveAll(Arrays.asList(courseAlgo, courseWebDev, courseMath));
            lecturerRepository.saveAll(Arrays.asList(lecturerDoe, lecturerSmith)); // Save lecturers again after course
                                                                                  // association

            // Setup Content items with diverse data
            content1 = new Content("Java Basics", "Introduction to Java programming.", ContentCategory.PDF, facultyCS, courseAlgo,
                    lecturerDoe, testUser, LocalDate.now().minusDays(5), 0, 0);
            content2 = new Content("HTML & CSS Guide", "Guide for web frontend.", ContentCategory.PDF, facultyCS,
                    courseWebDev, lecturerSmith, adminUser, LocalDate.now().minusDays(2), 1, 2);
            content3 = new Content("Calculus Cheat Sheet", "Important formulas for calculus.", ContentCategory.IMAGE,
                    facultyEng, courseMath, lecturerSmith, testUser, LocalDate.now(), 1, 100);
            content4 = new Content("Advanced Algorithms", "Deep dive into complex algorithms.", ContentCategory.PDF,
                    facultyCS, courseAlgo, lecturerDoe, adminUser, LocalDate.now().minusDays(10), 10, 0);
            content5 = new Content("Web App Security", "Security aspects of web applications.", ContentCategory.ZIP,
                    facultyCS, courseWebDev, lecturerSmith, testUser, LocalDate.now().minusDays(1), 0, 0);

            contentRepository.saveAll(Arrays.asList(content1, content2, content3, content4, content5));
        };
    }
}