package com.example.demo.dao.user;
import com.example.demo.models.dto.User;
import com.example.demo.service.game.GameService;
import com.example.demo.service.registration.RegistrationService;
import com.example.demo.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(
            UserService userService,
            RegistrationService registrationService,
            GameService gameService
    ) {

        return args -> {
            log.info("Preloading " + registrationService.registerUser("Player X9", "123qwerty"));
            log.info("Preloading " + registrationService.registerUser("Player Y9", "54321"));
            log.info("Preloading " + registrationService.registerUser("Player Z9", "dogdog"));
            log.info("Preloading " + registrationService.registerUser("Player M9", "catcat"));
            log.info("Preloading " + registrationService.registerUser("Player N9", "noyes"));

            var allUsers = userService.getAll();
            User testUser1 = allUsers.get(0);
            User testUser2 = allUsers.get(1);


            log.info("LOAD GAMESTATE with id " + gameService.setGame(testUser1, testUser2));
        };
    }
}