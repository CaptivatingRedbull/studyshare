package de.studyshare.studyshare;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.studyshare.studyshare.domain.Role;
import de.studyshare.studyshare.domain.User;
import de.studyshare.studyshare.repository.UserRepository;

@SpringBootApplication
public class StudyShareApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(StudyShareApplication.class);
	private final UserRepository userRepository;


	public StudyShareApplication(UserRepository userRepository) {
		this.userRepository = userRepository;

	}

	public static void main(String[] args) {
		SpringApplication.run(StudyShareApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		User user1 = new User("Max", "Mustermann", "max.mustermann@hotmail.com", "xxxxxxxxx", Role.STUDENT, "mmustermann");
		User user2 = new User("Maria", "Db", "maria.db@hotmail.com", "xxxxxxxxx", Role.ADMIN, "mariadb");
		userRepository.saveAll(Arrays.asList(user1, user2));

		logger.info("Users loaded from database:");
		for (User user : userRepository.findAll()) {
			logger.info("FirstName: {}, LastName: {}", user.getFirstName(), user.getLastName());
		}
	}

}

