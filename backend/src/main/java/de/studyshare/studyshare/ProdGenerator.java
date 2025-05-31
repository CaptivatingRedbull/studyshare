package de.studyshare.studyshare;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import de.studyshare.studyshare.domain.Role;
import de.studyshare.studyshare.domain.User;
import de.studyshare.studyshare.repository.UserRepository;

@Configuration
@Profile("prod")
public class ProdGenerator {

    @Value("${prod.user.username}")
    private String username;

    @Value("${prod.user.password}")
    private String userpassword;
    
    @Bean
    public CommandLineRunner initializesUser(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ){
        return args -> {
            User user = new User("root", "root", "root@localhost.com", username, passwordEncoder.encode(userpassword), Role.ADMIN);
            userRepository.save(user);
        };
    }
}
