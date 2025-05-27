package de.studyshare.studyshare;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import de.studyshare.studyshare.repository.UserRepository;

/**
 * Main application class for the StudyShare application.
 * This class is responsible for bootstrapping the Spring Boot application.
 */
@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class StudyShareApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyShareApplication.class, args);
    }

    /**
     * CommandLineRunner bean to execute code after the application has started.
     * This can be used for initializing data or performing startup tasks.
     *
     * @param userRepository  the UserRepository to interact with user data
     * @param passwordEncoder the PasswordEncoder to encode passwords
     * @return a CommandLineRunner instance
     */
    @Bean
    public CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
        };
    }

}
