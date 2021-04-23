package com.example.demo.dao.user;
import com.example.demo.models.dto.User;
import com.example.demo.service.gamestate.GameStateService;
import com.example.demo.service.registration.RegistrationService;
import com.example.demo.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(
            UserService userService,
            RegistrationService registrationService,
            GameStateService gameStateService
    ) {

        return args -> {
            var secondUser = new User(13L, "M", "zhopa", 0L);
            var firstUser = new User(15L, "NG", "zhopa", 0L);
            var gameId = gameStateService.setGame(firstUser, secondUser);
            log.info("Game id " + gameId);
        };
    }
}