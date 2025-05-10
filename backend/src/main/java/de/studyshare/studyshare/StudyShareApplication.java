package de.studyshare.studyshare;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.studyshare.studyshare.domain.Role;
import de.studyshare.studyshare.domain.User;
import de.studyshare.studyshare.repository.UserRepository;

@SpringBootApplication
public class StudyShareApplication {

    private static final Logger logger = LoggerFactory.getLogger(StudyShareApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(StudyShareApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            if (userRepository.findByUsername("mmustermann").isEmpty()) {
                User user1 = new User("Max", "Mustermann", "max.mustermann@hotmail.com",
                        "mmustermann", passwordEncoder.encode("password123"), Role.STUDENT);
                userRepository.save(user1);
                logger.info("Created user: mmustermann");
            }

            if (userRepository.findByUsername("mariadb").isEmpty()) {
                User user2 = new User("Maria", "Db", "maria.db@hotmail.com",
                        "mariadb", passwordEncoder.encode("adminpass"), Role.ADMIN);
                userRepository.save(user2);
                logger.info("Created user: mariadb");
            }

            logger.info("Users loaded from database:");
            for (User user : userRepository.findAll()) {
                logger.info("Username: {}, Role: {}", user.getUsername(), user.getRole());
            }
        };
    }
}
