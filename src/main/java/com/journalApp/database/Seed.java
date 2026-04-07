package com.journalApp.database;

import com.journalApp.entity.User;
import com.journalApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Profile("dev")
@Component
@RequiredArgsConstructor
public class Seed implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
    seedAdmin();
    }

    private void seedAdmin(){
        if (userRepository.existsByUsername("Admin") || userRepository.existsByEmail("patilshreyas547@gmail.com")) {
            log.info("♻ Admin or patilshreyas547@gmail.com already in use Reusing Admin");
            return;
        }
        User admin=User.builder().username("Admin")
                .password(passwordEncoder.encode("admin@123"))
                .email("patilshreyas547@gmail.com")
                .roles(List.of("ADMIN"))
                .sentimentAnalysis(true)
                .build();

        userRepository.save(admin);

        log.info("✅ Admin seeded successfully");
    }
}
