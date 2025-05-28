package de.studyshare.studyshare;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling; // Import for @Scheduled
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import de.studyshare.studyshare.repository.UserRepository;

/**
 * Main application class for the StudyShare application.
 * This class initializes the Spring Boot application,
 * enabling JPA repositories, transaction management, and Spring Data web
 * support.
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EnableScheduling // Enable scheduling for cleanup task
public class StudyShareApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyShareApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
        };
    }
}